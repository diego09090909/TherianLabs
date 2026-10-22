package cl.therionlabs.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CarreraDao{
    @Query("SELECT * FROM carreras ORDER BY nombre ASC")
    fun getTodasLasCarreras(): Flow<List<CarreraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarreras(carreras: List<CarreraEntity>)

    @Query("SELECT COUNT(*) FROM carreras")
    suspend fun getCantidadCarreras(): Int
}
