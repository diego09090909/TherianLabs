package cl.therionlabs.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import cl.therionlabs.myapplication.sync.SyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import com.google.gson.JsonParseException
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