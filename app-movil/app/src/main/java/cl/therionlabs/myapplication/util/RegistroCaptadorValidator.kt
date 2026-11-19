package cl.therionlabs.myapplication.util

object RegistroCaptadorValidator {

    sealed class Resultado {
        data object Valido : Resultado()
        data class Error(val mensaje: String) : Resultado()
    }

    fun validarFormulario(
        correo: String,
        clave: String,
        confirmarClave: String
    ): Resultado {
        val correoLimpio = correo.trim()

        if (correoLimpio.isBlank() || clave.isBlank() || confirmarClave.isBlank()) {
            return Resultado.Error("Por favor, completa todos los campos.")
        }

        if (!Validadores.correoValido(correoLimpio)) {
            return Resultado.Error("Ingresa un correo electrónico válido.")
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