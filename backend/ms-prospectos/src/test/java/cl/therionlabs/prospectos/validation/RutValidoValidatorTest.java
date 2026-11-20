package cl.therionlabs.prospectos.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RutValidoValidatorTest {

    private final RutValidoValidator validator = new RutValidoValidator();

    @Test
    void aceptaRutValidoConOSinFormato() {
        assertTrue(validator.isValid("12345678-5", null));
        assertTrue(validator.isValid("12.345.678-5", null));
    }

    @Test
    void dejaPasarNuloYVacioParaQueLoRevisenOtrasReglas() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("  ", null));
    }

    @Test
    void rechazaRutInvalido() {
        assertFalse(validator.isValid("12345678-9", null));
        assertFalse(validator.isValid("abc", null));
    }
}
