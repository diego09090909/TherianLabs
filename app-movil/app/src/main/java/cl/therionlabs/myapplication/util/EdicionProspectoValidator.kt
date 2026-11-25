package cl.therionlabs.myapplication.util

// Valida el formulario de edición de un prospecto (correo, celular y nivel de interés).
object EdicionProspectoValidator {

    val NIVELES = listOf("Alto", "Medio", "Bajo")

    /** Devuelve el mensaje de error, o null si los datos son válidos. */
    fun validar(correo: String, telefono: String, nivelInteres: String): String? = when {
        !Validadores.correoValido(correo) -> "Ingresa un correo válido, por ejemplo nombre@correo.cl."
        !Validadores.telefonoValido(telefono) -> "El celular debe ser chileno: 9XXXXXXXX, con o sin +56."
        nivelInteres !in NIVELES -> "Elige el nivel de interés."
        else -> null
    }
}
