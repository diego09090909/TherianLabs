package cl.therionlabs.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import cl.therionlabs.myapplication.sync.SyncScheduler
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class ProspectoRepository(
    private val context: Context,
    private val dao: ProspectoDao,
    private val api: ApiService
) {

    fun observarTodos(): Flow<List<Prospecto>> = dao.observarTodos()

    fun contarPendientes(): Flow<Int> = dao.contarPendientes()

    // Siempre guarda primero en local (offline-first). Devuelve false si el RUT ya existe en ese evento.
    suspend fun guardar(prospecto: Prospecto): Boolean {
        return try {
            dao.insertar(prospecto)
            SyncScheduler.programarAhora(context) // se ejecuta apenas haya red
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    // Envia los pendientes en lotes de 100. Devuelve true si no queda nada pendiente.
    suspend fun sincronizarPendientes(): Boolean {
        val pendientes = dao.obtenerPendientes()
        if (pendientes.isEmpty()) return true

        for (lote in pendientes.chunked(100)) {
            val dtos = lote.map {
                ProspectoDto(
                    eventoId = it.eventoId,
                    nombreCompleto = it.nombreCompleto,
                    rut = it.rut,
                    correo = CryptoManager.descifrar(it.correoCifrado),
                    telefono = CryptoManager.descifrar(it.telefonoCifrado),
                    colegio = it.colegio,
                    nivelEducativo = it.nivelEducativo,
                    carrera = it.carrera,
                    modalidad = it.modalidad,
                    observaciones = it.observaciones,
                    fechaCaptura = it.fechaCaptura
                )
            }
            try {
                val respuesta = api.enviarLote(dtos)
                if (respuesta.isSuccessful) {
                    dao.marcarSincronizados(lote.map { it.id })
                } else {
                    return false // el servidor rechazo: queda PENDIENTE y se reintenta
                }
            } catch (e: IOException) {
                return false // sin red: queda PENDIENTE y se reintenta
            }
        }
        return true
    }
}
