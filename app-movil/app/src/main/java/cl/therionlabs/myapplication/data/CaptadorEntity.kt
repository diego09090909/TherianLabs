
package cl.therionlabs.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "captadores")
data class CaptadorEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val nombreCompleto: String,
    val rut: String,
    val correo: String,
    val telefono: String,

    // Estado del registro para la futura sincronización
    val estadoSincronizacion: String = "PENDIENTE",

    val fechaCreacion: Long = System.currentTimeMillis()
)
