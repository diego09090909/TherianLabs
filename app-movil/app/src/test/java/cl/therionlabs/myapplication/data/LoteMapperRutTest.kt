package cl.therionlabs.myapplication.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoteMapperRutTest {

    @Test
    fun ignoraPuntosGuionYMayusculas() {
        assertTrue(LoteMapper.mismoRut("12.345.678-5", "123456785"))
        assertTrue(LoteMapper.mismoRut("11.111.111-k", "11111111-K"))
    }

    @Test
    fun rutDistintoNoCoincide() {
        assertFalse(LoteMapper.mismoRut("12.345.678-5", "12.345.679-3"))
    }

    @Test
    fun nuloOVacioNuncaCoincide() {
        assertFalse(LoteMapper.mismoRut(null, null))
        assertFalse(LoteMapper.mismoRut("", ""))
        assertFalse(LoteMapper.mismoRut("12345678-5", null))
    }
}
