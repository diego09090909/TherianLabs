package cl.therionlabs.ms_captadores.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

class RutValidatorTest {

    private final RutValidator validator = new RutValidator();

    @ParameterizedTest
    @ValueSource(strings = {"12345678-5", "12.345.678-5", "18123456-3", "7654321-6", "11111111-1", "6000000-K"})
    void aceptaRutsConDigitoCorrecto(String rut) {
        assertTrue(validator.isValid(rut, null), rut);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678-4", "12345678", "1234-5", "abc-d", "123456789-5", "12345678-X"})
    void rechazaRutsInvalidos(String rut) {
        assertFalse(validator.isValid(rut, null), rut);
    }

    @Test
    void nuloOVacioLoDejaALasOtrasAnotaciones() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("  ", null));
    }
}
