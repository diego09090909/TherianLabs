package cl.therionlabs.prospectos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RutValidatorTest {

    @Test
    void rutConDigitoCorrectoEsValido() {
        assertTrue(RutValidator.esValido("12345678-5"));
        assertTrue(RutValidator.esValido("11111111-1"));
        assertTrue(RutValidator.esValido("7654321-6"));
    }

    @Test
    void rutConDigitoKYCeroEsValido() {
        assertTrue(RutValidator.esValido("10000013-K"));
        assertTrue(RutValidator.esValido("10000013-k"));
        assertTrue(RutValidator.esValido("10000004-0"));
    }

    @Test
    void rutConDigitoIncorrectoEsInvalido() {
        assertFalse(RutValidator.esValido("12345678-9"));
        assertFalse(RutValidator.esValido("11111111-2"));
    }

    @Test
    void rutConFormatoRaroEsInvalido() {
        assertFalse(RutValidator.esValido(null));
        assertFalse(RutValidator.esValido(""));
        assertFalse(RutValidator.esValido("abc"));
        assertFalse(RutValidator.esValido("123-4"));
    }

    @Test
    void normalizarQuitaPuntosYAgregaGuion() {
        assertEquals("12345678-5", RutValidator.normalizar("12.345.678-5"));
        assertEquals("12345678-5", RutValidator.normalizar("123456785"));
        assertEquals("10000013-K", RutValidator.normalizar("10.000.013-k"));
        assertNull(RutValidator.normalizar(null));
    }

    @Test
    void esValidoAceptaRutSinGuionNiPuntos() {
        assertTrue(RutValidator.esValido("123456785"));
        assertTrue(RutValidator.esValido("12.345.678-5"));
    }
}
