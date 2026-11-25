package cl.therionlabs.myapplication.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class EdicionProspectoValidatorTest {

    @Test
    fun datosValidosNoDanError() {
        assertNull(EdicionProspectoValidator.validar("ana@correo.cl", "912345678", "Alto"))
    }

    @Test
    fun aceptaCelularConPrefijoYEspacios() {
        assertNull(EdicionProspectoValidator.validar("ana@correo.cl", "+56 9 1234 5678", "Medio"))
    }

    @Test
    fun correoInvalidoDaError() {
        assertNotNull(EdicionProspectoValidator.validar("ana@", "912345678", "Alto"))
    }

    @Test
    fun telefonoInvalidoDaError() {
        assertNotNull(EdicionProspectoValidator.validar("ana@correo.cl", "12345", "Alto"))
    }

    @Test
    fun nivelDesconocidoDaError() {
        assertNotNull(EdicionProspectoValidator.validar("ana@correo.cl", "912345678", "Altisimo"))
        assertNotNull(EdicionProspectoValidator.validar("ana@correo.cl", "912345678", ""))
    }

    @Test
    fun losNivelesSonLosDelFormulario() {
        assertEquals(listOf("Alto", "Medio", "Bajo"), EdicionProspectoValidator.NIVELES)
    }
}
