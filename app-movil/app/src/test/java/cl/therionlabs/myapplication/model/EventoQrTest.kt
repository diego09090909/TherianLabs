package cl.therionlabs.myapplication.model

import cl.therionlabs.myapplication.data.EventoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class EventoQrTest {

    @Test
    fun codigoConocidoDevuelveElEventoDeDemostracion() {
        assertSame(EventosDemo.feriaVocacional, EventosDemo.desdeCodigo(" evt-0042 "))
        assertEquals("Colegio San Mateo", EventosDemo.desdeCodigo("EVT-0043").liceo)
    }

    @Test
    fun codigoDesconocidoCreaUnEventoPorConfirmar() {
        val evento = EventosDemo.desdeCodigo(" x-77 ")
        assertEquals("X-77", evento.id)
        assertEquals("Por confirmar", evento.liceo)
        assertEquals("Evento X-77", evento.nombre)
        assertEquals("-", evento.fecha)
    }

    @Test
    fun eventoDtoSeConvierteConFechaLegible() {
        val qr = EventoDto("EVT-0050", "Feria de Carreras", "Liceo Industrial", "2026-10-14", "ABIERTO").aEventoQr()
        assertEquals("EVT-0050", qr.id)
        assertEquals("Feria de Carreras", qr.nombre)
        assertEquals("Liceo Industrial", qr.liceo)
        assertTrue(qr.fecha.contains("14"))
        assertTrue(qr.fecha.contains("2026"))
    }

    @Test
    fun eventoDtoConFechaRotaConservaElTextoOriginal() {
        val qr = EventoDto("EVT-0051", "Feria", "Liceo", "sin-fecha", "ABIERTO").aEventoQr()
        assertEquals("sin-fecha", qr.fecha)
    }

    @Test
    fun resultadosDeEventoGuardanElEvento() {
        val encontrado = ResultadoEvento.Encontrado(EventosDemo.feriaVocacional)
        val sinConexion = ResultadoEvento.SinConexion(EventosDemo.feriaVocacional)
        assertEquals("EVT-0042", encontrado.evento.id)
        assertEquals("EVT-0042", sinConexion.evento.id)
        assertEquals("Cerrado", ResultadoEvento.Cerrado::class.simpleName)
        assertEquals("NoExiste", ResultadoEvento.NoExiste::class.simpleName)
    }
}
