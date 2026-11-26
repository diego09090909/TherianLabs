package cl.therionlabs.myapplication.util

object RegistroCaptadorValidator {

    sealed class Resultado {
        data object Valido : Resultado()
        data class Error(val mensaje: String) : Resultado()
    }

    // Mismos dominios que acepta ms-captadores (CorreoDuocValidator)
    private val REGEX_CORREO_DUOC =
        Regex("^[a-z0-9._%+-]+@(duocuc\\.cl|duoc\\.cl|profesor\\.duoc\\.cl)$")

    fun correoDuocValido(correo: String): Boolean =
        REGEX_CORREO_DUOC.matches(correo.trim().lowercase())

    fun validarFormulario(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        clave: String,
        confirmarClave: String
    ): Resultado {
        val correoLimpio = correo.trim()

        if (nombre.isBlank() || rut.isBlank() || correoLimpio.isBlank() ||
            telefono.isBlank() || clave.isBlank() || confirmarClave.isBlank()
        ) {
            return Resultado.Error("Por favor, completa todos los campos.")
        }

        if (nombre.trim().length < 3) {
            return Resultado.Error("Ingresa tu nombre completo.")
        }

        if (!Validadores.rutValido(rut)) {
            return Resultado.Error("El RUT no es válido. Revisa el dígito verificador.")
        }

        if (!Validadores.correoValido(correoLimpio)) {
            return Resultado.Error("Ingresa un correo electrónico válido.")
        }

        if (!correoDuocValido(correoLimpio)) {
            return Resultado.Error("Debes usar tu correo Duoc (@duocuc.cl o @duoc.cl).")
        }

        if (!Validadores.telefonoValido(telefono)) {
            return Resultado.Error("Ingresa un celular válido (9XXXXXXXX).")
        }

        if (clave != confirmarClave) {
            return Resultado.Error("Las contraseñas no coinciden.")
        }

        if (clave.length < 6) {
            return Resultado.Error("La contraseña debe tener al menos 6 caracteres.")
        }

        return Resultado.Valido
    }
}
