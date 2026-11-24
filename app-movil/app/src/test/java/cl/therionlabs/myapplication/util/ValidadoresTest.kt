package cl.therionlabs.myapplication.util

import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadoresTest {

    @Test
    fun normalizarRut_quitaPuntosYPoneGuion() {
        assertEquals("12345678-5", Validadores.normalizarRut("12.345.678-5"))
        assertEquals("12345678-5", Validadores.normalizarRut("123456785"))
        assertEquals("6000000-K", Validadores.normalizarRut("6.000.000-k"))
    }

    @Test
    fun normalizarRut_textoMuyCortoQuedaIgual() {
        assertEquals("1", Validadores.normalizarRut("1"))
        assertEquals("", Validadores.normalizarRut(""))
    }

    @Test
    fun formatearRutEnVivo_agregaGuionAlLlegarANueveCaracteres() {
        assertEquals("12345678-5", Validadores.formatearRutEnVivo("123456785"))
        assertEquals("1234567", Validadores.formatearRutEnVivo("1234567"))
        assertEquals("12345678-9", Validadores.formatearRutEnVivo("1234567890"))
    }

    @Test
    fun formatearRutEnVivo_respetaGuionEscritoAMano() {
        assertEquals("12345678-K", Validadores.formatearRutEnVivo("12.345.678-k"))
        assertEquals("12345678-", Validadores.formatearRutEnVivo("12345678-"))
        assertEquals("", Validadores.formatearRutEnVivo("-5"))
        assertEquals("", Validadores.formatearRutEnVivo(""))
    }

    @Test
    fun formatearRutEnVivo_ignoraLetrasQueNoSonK() {
        assertEquals("1234", Validadores.formatearRutEnVivo("12ab34"))
    }

    @Test
    fun rutValido_aceptaDigitosVerificadoresCorrectos() {
        listOf("12345678-5", "12.345.678-5", "18123456-3", "7654321-6", "11111111-1", "6000000-K", "6000000-k", "20000008-0")
            .forEach { assertTrue(it, Validadores.rutValido(it)) }
    }

    @Test
    fun rutValido_rechazaDigitoIncorrectoOFormatoRaro() {
        listOf("12345678-4", "1234-5", "123456789-5", "1234A67-8", "", "abc")
            .forEach { assertFalse(it, Validadores.rutValido(it)) }
    }

    @Test
    fun correoValido_aceptaCorreosNormales() {
        assertTrue(Validadores.correoValido("ke.gonzalezo@duocuc.cl"))
        assertTrue(Validadores.correoValido("  usuario+tag@correo.com  "))
    }

    @Test
    fun correoValido_rechazaCorreosMalFormados() {
        listOf("", "sin-arroba", "a@b", "a b@c.cl", "@duocuc.cl", "a@.cl")
            .forEach { assertFalse(it, Validadores.correoValido(it)) }
    }

    @Test
    fun telefonoValido_aceptaCelularesChilenos() {
        assertTrue(Validadores.telefonoValido("912345678"))
        assertTrue(Validadores.telefonoValido("+56912345678"))
        assertTrue(Validadores.telefonoValido("56 9 1234 5678"))
    }

    @Test
    fun telefonoValido_rechazaOtrosNumeros() {
        listOf("", "812345678", "91234567", "9123456789", "abc")
            .forEach { assertFalse(it, Validadores.telefonoValido(it)) }
    }

    @Test
    fun formatearFecha_agregaGuionesMientrasSeEscribe() {
        assertEquals("01-02-2008", Validadores.formatearFecha("01022008"))
        assertEquals("01-02", Validadores.formatearFecha("0102"))
        assertEquals("01", Validadores.formatearFecha("01"))
        assertEquals("", Validadores.formatearFecha(""))
    }

    @Test
    fun formatearFecha_ignoraLoQueNoSeaNumeroYCortaEnOchoDigitos() {
        assertEquals("01-02-2008", Validadores.formatearFecha("ab01/02/2008999"))
    }

    private fun anioActual() = Calendar.getInstance().get(Calendar.YEAR)

    @Test
    fun fechaNacimiento_aceptaFechasReales() {
        assertTrue(Validadores.fechaNacimientoValida("15-06-2005"))
        assertTrue(Validadores.fechaNacimientoValida("31-12-2008"))
        assertTrue(Validadores.fechaNacimientoValida("01-01-${anioActual()}"))
    }

    @Test
    fun fechaNacimiento_manejaAniosBisiestos() {
        assertTrue(Validadores.fechaNacimientoValida("29-02-2004"))
        assertTrue(Validadores.fechaNacimientoValida("29-02-2000"))
        assertFalse(Validadores.fechaNacimientoValida("29-02-2005"))
    }

    @Test
    fun fechaNacimiento_rechazaDiasYMesesImposibles() {
        listOf("31-04-2005", "30-02-2005", "00-01-2005", "15-00-2005", "15-13-2005", "32-01-2005")
            .forEach { assertFalse(it, Validadores.fechaNacimientoValida(it)) }
    }

    @Test
    fun fechaNacimiento_rechazaFormatosIncorrectos() {
        listOf("", "1-1-2005", "15/06/2005", "2005-06-15", "15-06-05")
            .forEach { assertFalse(it, Validadores.fechaNacimientoValida(it)) }
    }

    @Test
    fun fechaNacimiento_rechazaFuturoYMuyAntigua() {
        assertFalse(Validadores.fechaNacimientoValida("01-01-${anioActual() + 1}"))
        assertFalse(Validadores.fechaNacimientoValida("01-01-${anioActual() - 61}"))
        assertTrue(Validadores.fechaNacimientoValida("01-01-${anioActual() - 60}"))
    }

    @Test
    fun fechaNacimiento_rechazaFechaPosteriorAHoyDentroDelMismoAnio() {
        val hoy = Calendar.getInstance()
        val mes = hoy.get(Calendar.MONTH) + 1
        val dia = hoy.get(Calendar.DAY_OF_MONTH)
        val hoyTexto = "%02d-%02d-%d".format(dia, mes, hoy.get(Calendar.YEAR))
        assertTrue(Validadores.fechaNacimientoValida(hoyTexto))

        if (mes < 12) {
            val mesSiguiente = "01-%02d-%d".format(mes + 1, hoy.get(Calendar.YEAR))
            assertFalse(Validadores.fechaNacimientoValida(mesSiguiente))
        }
        if (dia < 28) {
            val diaSiguiente = "%02d-%02d-%d".format(dia + 1, mes, hoy.get(Calendar.YEAR))
            assertFalse(Validadores.fechaNacimientoValida(diaSiguiente))
        }
    }
}
