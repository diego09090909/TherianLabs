package cl.therionlabs.myapplication.util

object LoginValidator {

    sealed class Resultado {
        data object Valido : Resultado()
        data class Error(val mensaje: String) : Resultado()
    }

    fun validarFormulario(correo: String, clave: String): Resultado {
        val correoLimpio = correo.trim()

        if (correoLimpio.isBlank() || clave.isBlank()) {
            return Resultado.Error("Por favor, ingresa tu correo y contraseña.")
        }

        if (!Validadores.correoValido(correoLimpio)) {
            return Resultado.Error("El formato del correo es inválido. Ej: usuario@correo.cl")
        }

        return Resultado.Valido
    }
}