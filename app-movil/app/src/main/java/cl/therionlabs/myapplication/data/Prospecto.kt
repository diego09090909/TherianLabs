package cl.therionlabs.myapplication.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object Estado {
    const val PENDIENTE = "PENDIENTE"
    const val SINCRONIZADO = "SINCRONIZADO"
}

// Tabla local. correo y telefono se guardan CIFRADOS (RF-04).
// Los campos desde `carrera2` hacia abajo vienen del cupón de admisión (versión 4 de la base).
@Entity(
    tableName = "prospectos",
    indices = [Index(value = ["rut", "eventoId"], unique = true)]
)
data class Prospecto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventoId: String,
    val nombreCompleto: String,          // "Nombre Apellidos"
    val rut: String,
    val correoCifrado: String,
    val telefonoCifrado: String,
    val colegio: String,                 // "Establecimiento" del cupón
    val nivelEducativo: String,          // "4° medio", "Egresado" u "Otra: ..."
    val carrera: String,                 // 1ª opción (prioridad 1)
    val modalidad: String,
    val observaciones: String,
    val fechaCaptura: Long,
    val estado: String = Estado.PENDIENTE,
    val carrera2: String = "",           // 2ª opción
    val carrera3: String = "",           // 3ª opción
    val sexo: String = "",               // Femenino / Masculino / No indica
    val fechaNacimiento: String = "",    // dd-MM-aaaa
    val comunaResidencia: String = "",
    val comunaColegio: String = "",
    val jornada: String = "",            // Diurna / Vespertina
    val sedes: String = "",              // sedes separadas por "|"
    val nivelInteres: String = "",       // Alto / Medio / Bajo
    val afirmacion: String = "",         // "1" a "4"
    val autorizaDatos: Boolean = false   // "Sí, autorizo" (Consentimiento Informado)
)