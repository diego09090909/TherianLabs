package cl.therionlabs.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import cl.therionlabs.myapplication.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.IOException

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
                    fechaCaptura = it.fechaCaptura,
                    carrera2 = it.carrera2,
                    carrera3 = it.carrera3,
                    sexo = it.sexo,
                    fechaNacimiento = it.fechaNacimiento,
                    comunaResidencia = it.comunaResidencia,
                    comunaColegio = it.comunaColegio,
                    jornada = it.jornada,
                    sedes = it.sedes.split("|").filter { s -> s.isNotBlank() },
                    nivelInteres = it.nivelInteres,
                    afirmacion = it.afirmacion,
                    autorizaDatos = it.autorizaDatos
                )
            }
            try {
                val respuesta = api.enviarLote(dtos)
                if (respuesta.isSuccessful) {
                    dao.marcarSincronizados(lote.map { it.id })
                } else {
                    return false
                }
            } catch (e: IOException) {
                return false
            }
        }
        return true
    }
}