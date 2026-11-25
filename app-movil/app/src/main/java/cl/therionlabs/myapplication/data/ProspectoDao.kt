package cl.therionlabs.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProspectoDao {

    @Insert
    suspend fun insertar(prospecto: Prospecto): Long

    @Query("SELECT * FROM prospectos ORDER BY fechaCaptura DESC")
    fun observarTodos(): Flow<List<Prospecto>>

    @Query("SELECT COUNT(*) FROM prospectos WHERE estado = 'PENDIENTE'")
    fun contarPendientes(): Flow<Int>

    @Query("SELECT * FROM prospectos WHERE estado = 'PENDIENTE'")
    suspend fun obtenerPendientes(): List<Prospecto>

    @Query("SELECT * FROM prospectos WHERE estado IN ('PENDIENTE', 'RECHAZADO')")
    suspend fun obtenerPendientesYRechazados(): List<Prospecto>

    @Query("UPDATE prospectos SET estado = 'SINCRONIZADO', motivoRechazo = '' WHERE id IN (:ids)")
    suspend fun marcarSincronizados(ids: List<Int>)

    @Query(
        "UPDATE prospectos SET correoCifrado = :correo, telefonoCifrado = :telefono, " +
            "nivelInteres = :nivel, estado = :estado, motivoRechazo = '' WHERE id = :id"
    )
    suspend fun actualizarContacto(id: Int, correo: String, telefono: String, nivel: String, estado: String)

    @Query("DELETE FROM prospectos WHERE id = :id")
    suspend fun eliminar(id: Int)

    @Query("UPDATE prospectos SET estado = 'RECHAZADO', motivoRechazo = :motivo WHERE id = :id")
    suspend fun marcarRechazado(id: Int, motivo: String)
}
