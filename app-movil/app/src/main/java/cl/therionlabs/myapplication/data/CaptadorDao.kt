
package cl.therionlabs.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CaptadorDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(captador: CaptadorEntity)

    @Query(
        "SELECT * FROM captadores ORDER BY fechaCreacion DESC"
    )
    suspend fun obtenerTodos(): List<CaptadorEntity>

    @Query(
        """
        SELECT * FROM captadores
        WHERE estadoSincronizacion = 'PENDIENTE'
        ORDER BY fechaCreacion ASC
        """
    )
    suspend fun obtenerPendientes(): List<CaptadorEntity>

    @Query(
        """
        UPDATE captadores
        SET estadoSincronizacion = 'SINCRONIZADO'
        WHERE id = :captadorId
        """
    )
    suspend fun marcarSincronizado(captadorId: String)
}
