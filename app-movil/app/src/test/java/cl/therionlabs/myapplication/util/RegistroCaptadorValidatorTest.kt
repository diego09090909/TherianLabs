package cl.therionlabs.myapplication.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RegistroCaptadorValidatorTest {

    private fun validar(
        nombre: String = "Ana Pérez",
        rut: String = "12.345.678-5",
        correo: String = "ana@duocuc.cl",
        telefono: String = "987654321",
        clave: String = "clave123",
        confirmar: String = "clave123"
    ) = RegistroCaptadorValidator.validarFormulario(nombre, rut, correo, telefono, clave, confirmar)

    private fun error(m: String) = RegistroCaptadorValidator.Resultado.Error(m)

    @Test
    fun datosCorrectosPasan() {
        assertEquals(RegistroCaptadorValidator.Resultado.Valido, validar())
    }

    @Test
    fun camposVaciosPidenCompletarTodo() {
        val esperado = error("Por favor, completa todos los campos.")
        assertEquals(esperado, validar(nombre = ""))
        assertEquals(esperado, validar(rut = ""))
        assertEquals(esperado, validar(correo = ""))
        assertEquals(esperado, validar(telefono = ""))
        assertEquals(esperado, validar(clave = ""))
        assertEquals(esperado, validar(confirmar = ""))
    }

    @Test
    fun rutInvalidoDaError() {
        assertEquals(error("El RUT no es válido. Revisa el dígito verificador."), validar(rut = "12.345.678-9"))
    }

    @Test
    fun correoInvalidoDaError() {
        assertEquals(error("Ingresa un correo electrónico válido."), validar(correo = "ana"))
    }

    @Test
    fun correoNoDuocDaError() {
        assertEquals(error("Debes usar tu correo Duoc (@duocuc.cl o @duoc.cl)."), validar(correo = "ana@gmail.com"))
    }

    @Test
    fun telefonoInvalidoDaError() {
        assertEquals(error("Ingresa un celular válido (9XXXXXXXX)."), validar(telefono = "12345"))
    }

    @Test
    fun contrasenasDistintasDanError() {
        assertEquals(error("Las contraseñas no coinciden."), validar(confirmar = "clave124"))
    }

    @Test
    fun contrasenaCortaDaError() {
        assertEquals(error("La contraseña debe tener al menos 6 caracteres."), validar(clave = "12345", confirmar = "12345"))
    }
}
