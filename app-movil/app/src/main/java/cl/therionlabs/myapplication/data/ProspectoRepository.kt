package cl.therionlabs.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import cl.therionlabs.myapplication.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import com.google.gson.JsonParseException
import java.io.IOException

// Resultado de editar o eliminar un prospecto que puede estar ya en el servidor.
enum class ResultadoRemoto { OK, SIN_CONEXION, ERROR_SERVIDOR }

class ProspectoRepository(
    private val context: Context,
    private val dao: ProspectoDao,
    private val api: ApiService,               // Tipo exacto de la interfaz de Retrofit
    private val scope: CoroutineScope          // Scope para corrutinas en segundo plano
) {

    fun observarTodos(): Flow<List<Prospecto>> = dao.observarTodos()

    fun contarPendientes(): Flow<Int> = dao.contarPendientes()

    suspend fun guardar(prospecto: Prospecto): Boolean {
        return try {
            dao.insertar(prospecto)
            SyncScheduler.programarAhora(context)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    // Busca el id que tiene el prospecto en ms-prospectos (la app solo guarda su id local).
    private suspend fun idRemoto(p: Prospecto): Long? {
        val respuesta = api.listarPorEvento(p.eventoId)
        check(respuesta.isSuccessful) { "HTTP ${respuesta.code()}" }
        return respuesta.body()?.firstOrNull { LoteMapper.mismoRut(it.rut, p.rut) }?.id
    }

    // UPDATE: si ya esta en el servidor hace PUT; si aun no, solo cambia lo local y se enviara con el lote.
    suspend fun actualizarContacto(
        p: Prospecto,
        correo: String,
        telefono: String,
        nivelInteres: String
    ): ResultadoRemoto {
        val correoCifrado = CryptoManager.cifrar(correo)
        val telefonoCifrado = CryptoManager.cifrar(telefono)
        val editado = p.copy(
            correoCifrado = correoCifrado,
            telefonoCifrado = telefonoCifrado,
            nivelInteres = nivelInteres
        )
        if (p.estado == Estado.SINCRONIZADO) {
            try {
                val id = idRemoto(p) ?: return ResultadoRemoto.ERROR_SERVIDOR
                val respuesta = api.actualizar(id, LoteMapper.aDto(editado, correo, telefono))
                if (!respuesta.isSuccessful) return ResultadoRemoto.ERROR_SERVIDOR
            } catch (e: IOException) {
                return ResultadoRemoto.SIN_CONEXION
            } catch (e: JsonParseException) {
                return ResultadoRemoto.ERROR_SERVIDOR
            } catch (e: IllegalStateException) {
                return ResultadoRemoto.ERROR_SERVIDOR
            }
        }
        // Un registro rechazado que se corrige vuelve a quedar pendiente para reenviarse.
        val nuevoEstado = if (p.estado == Estado.RECHAZADO) Estado.PENDIENTE else p.estado
        dao.actualizarContacto(p.id, correoCifrado, telefonoCifrado, nivelInteres, nuevoEstado)
        if (nuevoEstado == Estado.PENDIENTE) SyncScheduler.programarAhora(context)
        return ResultadoRemoto.OK
    }

    // DELETE: si ya esta en el servidor lo borra alla (DELETE) y luego en Room.
    suspend fun eliminar(p: Prospecto): ResultadoRemoto {
        if (p.estado == Estado.SINCRONIZADO) {
            try {
                val id = idRemoto(p)
                if (id != null) {
                    val respuesta = api.eliminar(id)
                    if (!respuesta.isSuccessful && respuesta.code() != 404) return ResultadoRemoto.ERROR_SERVIDOR
                }
            } catch (e: IOException) {
                return ResultadoRemoto.SIN_CONEXION
            } catch (e: JsonParseException) {
                return ResultadoRemoto.ERROR_SERVIDOR
            } catch (e: IllegalStateException) {
                return ResultadoRemoto.ERROR_SERVIDOR
            }
        }
        dao.eliminar(p.id)
        return ResultadoRemoto.OK
    }

    suspend fun sincronizarPendientes(reintentarRechazados: Boolean = false): Boolean {
        val pendientes = if (reintentarRechazados) dao.obtenerPendientesYRechazados() else dao.obtenerPendientes()
        if (pendientes.isEmpty()) return true

        for (lote in pendientes.chunked(100)) {
            val dtos = lote.map {
                LoteMapper.aDto(
                    prospecto = it,
                    correo = CryptoManager.descifrar(it.correoCifrado),
                    telefono = CryptoManager.descifrar(it.telefonoCifrado)
                )
            }
            try {
                val respuesta = api.enviarLote(dtos)
                val cuerpo = respuesta.body()
                if (!respuesta.isSuccessful || cuerpo == null) return false
                val resultado = LoteMapper.interpretar(lote, cuerpo) ?: return false
                if (resultado.sincronizados.isNotEmpty()) {
                    dao.marcarSincronizados(resultado.sincronizados)
                }
                resultado.rechazados.forEach { (id, motivo) -> dao.marcarRechazado(id, motivo) }
            } catch (e: IOException) {
                return false
            } catch (e: JsonParseException) {
                return false
            }
        }
        return true
    }
}