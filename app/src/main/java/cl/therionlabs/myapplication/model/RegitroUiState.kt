package cl.therionlabs.myapplication.model

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: RegistroErrores = RegistroErrores()
)

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val aceptaTerminos: String? = null
)