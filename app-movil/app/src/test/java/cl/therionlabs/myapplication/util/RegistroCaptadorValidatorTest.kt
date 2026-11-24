package cl.therionlabs.myapplication.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RegistroCaptadorValidatorTest {

    @Test
    fun datosCorrectosPasan() {
        assertEquals(
            RegistroCaptadorValidator.Resultado.Valido,
            RegistroCaptadorValidator.validarFormulario("ana@duocuc.cl", "clave123", "clave123")
        )
    }

    @Test
    fun camposVaciosPidenCompletarTodo() {
        val esperado = RegistroCaptadorValidator.Resultado.Error("Por favor, completa todos los campos.")
        assertEquals(esperado, RegistroCaptadorValidator.validarFormulario("", "a", "a"))
        assertEquals(esperado, RegistroCaptadorValidator.validarFormulario("ana@duocuc.cl", "", "a"))
        assertEquals(esperado, RegistroCaptadorValidator.validarFormulario("ana@duocuc.cl", "a", ""))
    }

    @Test
    fun correoInvalidoDaError() {
        assertEquals(
            RegistroCaptadorValidator.Resultado.Error("Ingresa un correo electrónico válido."),
            RegistroCaptadorValidator.validarFormulario("ana", "clave123", "clave123")
        )
    }

    @Test
    fun contrasenasDistintasDanError() {
        assertEquals(
            RegistroCaptadorValidator.Resultado.Error("Las contraseñas no coinciden."),
            RegistroCaptadorValidator.validarFormulario("ana@duocuc.cl", "clave123", "clave124")
        )
    }

    @Test
    fun contrasenaCortaDaError() {
        assertEquals(
            RegistroCaptadorValidator.Resultado.Error("La contraseña debe tener al menos 6 caracteres."),
            RegistroCaptadorValidator.validarFormulario("ana@duocuc.cl", "12345", "12345")
        )
    }
}
