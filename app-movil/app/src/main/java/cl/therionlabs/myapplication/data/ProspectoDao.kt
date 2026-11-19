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

    @Query("UPDATE prospectos SET estado = 'SINCRONIZADO' WHERE id IN (:ids)")
    suspend fun marcarSincronizados(ids: List<Int>)
}
