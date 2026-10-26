package cl.therionlabs.myapplication.data



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carreras")
data class CarreraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val escuela: String,


)