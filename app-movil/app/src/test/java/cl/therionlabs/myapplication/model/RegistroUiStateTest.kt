package cl.therionlabs.myapplication.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistroUiStateTest {

    @Test
    fun estadoInicialEstaVacioYSinErrores() {
        val estado = RegistroUiState()
        assertEquals("", estado.rut)
        assertTrue(estado.sedes.isEmpty())
        assertFalse(estado.aceptaTerminos)
        assertEquals(0, estado.errores.cantidad())
    }

    @Test
    fun cantidadCuentaSoloLosErroresPresentes() {
        val errores = RegistroErrores(rut = "RUT inválido", correo = "Correo inválido", sedes = "Elige una sede")
        assertEquals(3, errores.cantidad())
    }

    @Test
    fun cantidadCuentaLosDieciseisCamposPosibles() {
        val todos = RegistroErrores(
            "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"
        )
        assertEquals(16, todos.cantidad())
    }

    @Test
    fun copiarEstadoNoAlteraElOriginal() {
        val original = RegistroUiState()
        val modificado = original.copy(rut = "12345678-5", sedes = setOf("Maipú"))
        assertEquals("", original.rut)
        assertEquals("12345678-5", modificado.rut)
        assertEquals(setOf("Maipú"), modificado.sedes)
    }

    @Test
    fun catalogoDeActividadesIncluyeLasTresOpciones() {
        val valores = Catalogos.actividades.map { it.valor }
        assertEquals(listOf("4° medio", Catalogos.ACTIVIDAD_EGRESADO, Catalogos.ACTIVIDAD_OTRA), valores)
        assertEquals("Otra", Catalogos.ACTIVIDAD_OTRA)
        assertEquals("Egresado", Catalogos.ACTIVIDAD_EGRESADO)
    }

    @Test
    fun catalogosSimplesTienenSusOpciones() {
        assertEquals(listOf("Femenino", "Masculino", "No indica"), Catalogos.sexos.map { it.valor })
        assertEquals(listOf("Diurna", "Vespertina"), Catalogos.jornadas.map { it.valor })
        assertEquals(listOf("Alto", "Medio", "Bajo"), Catalogos.nivelesInteres.map { it.valor })
        assertEquals(listOf("1", "2", "3", "4"), Catalogos.afirmaciones.map { it.valor })
    }

    @Test
    fun sedesPorRegionSumanDiecinueveSinRepetirse() {
        val sedes = Catalogos.sedesPorRegion.flatMap { it.sedes }.map { it.nombre }
        assertEquals(5, Catalogos.sedesPorRegion.size)
        assertEquals(19, sedes.size)
        assertEquals(sedes.size, sedes.toSet().size)
    }

    @Test
    fun comunasTienenOpcionesPorCadaComunaConSuRegion() {
        val total = Comunas.porRegion.sumOf { it.comunas.size }
        assertTrue(total > 300)
        assertEquals(total, Comunas.opciones.size)
        assertTrue(Comunas.porRegion.all { it.comunas.none { c -> c.isBlank() } })
        val maipu = Comunas.opciones.first { it.titulo == "Maipú" }
        assertTrue(maipu.detalle.startsWith("Región"))
        assertTrue(Comunas.opciones.any { it.titulo == "Santiago" })
    }
}
