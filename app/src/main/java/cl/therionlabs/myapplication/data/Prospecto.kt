package cl.therionlabs.myapplication.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object Estado {
    const val PENDIENTE = "PENDIENTE"
    const val SINCRONIZADO = "SINCRONIZADO"
}

// Tabla local. correo y telefono se guardan CIFRADOS (RF-04).
@Entity(
    tableName = "prospectos",
    indices = [Index(value = ["rut", "eventoId"], unique = true)]
)
data class Prospecto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventoId: String,
    val nombreCompleto: String,
    val rut: String,
    val correoCifrado: String,
    val telefonoCifrado: String,
    val colegio: String,
    val nivelEducativo: String,
    val carrera: String,
    val modalidad: String,
    val observaciones: String,
    val fechaCaptura: Long,
    val estado: String = Estado.PENDIENTE
)
