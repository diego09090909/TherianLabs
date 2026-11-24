package cl.therionlabs.myapplication.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun correoYClaveValidosPasan() {
        assertEquals(LoginValidator.Resultado.Valido, LoginValidator.validarFormulario("ana@duocuc.cl", "clave123"))
    }

    @Test
    fun correoConEspaciosSeLimpia() {
        assertEquals(LoginValidator.Resultado.Valido, LoginValidator.validarFormulario("  ana@duocuc.cl ", "x"))
    }

    @Test
    fun camposVaciosPidenCorreoYContrasena() {
        val esperado = LoginValidator.Resultado.Error("Por favor, ingresa tu correo y contraseña.")
        assertEquals(esperado, LoginValidator.validarFormulario("", "clave123"))
        assertEquals(esperado, LoginValidator.validarFormulario("ana@duocuc.cl", ""))
        assertEquals(esperado, LoginValidator.validarFormulario("   ", "  "))
    }

    @Test
    fun correoMalFormadoDaError() {
        assertEquals(
            LoginValidator.Resultado.Error("El formato del correo es inválido. Ej: usuario@correo.cl"),
            LoginValidator.validarFormulario("no-es-correo", "clave123")
        )
    }
}
