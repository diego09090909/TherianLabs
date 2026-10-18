package cl.therionlabs.myapplication.model

data class RegistroUiState(
    val eventoId: String = "EVENTO-001",
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val telefono: String = "",
    val colegio: String = "",
    val nivel: String = "",
    val carrera: String = "",
    val modalidad: String = "",
    val observaciones: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: RegistroErrores = RegistroErrores()
)

data class RegistroErrores(
    val nombre: String? = null,
    val rut: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val colegio: String? = null,
    val nivel: String? = null,
    val carrera: String? = null,
    val modalidad: String? = null,
    val aceptaTerminos: String? = null
)

object Catalogos {
    val niveles = listOf("1° medio", "2° medio", "3° medio", "4° medio", "Egresado")
    val carreras = listOf(
        "Analista Programador",
        "Informática",
        "Construcción",
        "Administración",
        "Salud",
        "Electricidad"
    )
    val modalidades = listOf("Online", "Presencial")
}
