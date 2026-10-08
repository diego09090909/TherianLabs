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

// Opciones de los desplegables (luego se pueden reemplazar por el catalogo oficial de cursos)
object Catalogos {
    val niveles = listOf("Enseñanza Media", "Técnico", "Universitario", "Otro")
    val carreras = listOf("Informática", "Construcción", "Administración", "Salud", "Electricidad")
    val modalidades = listOf("Online", "Presencial")
}
