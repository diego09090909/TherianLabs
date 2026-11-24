package cl.therionlabs.ms_captadores.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CorreoDuocValidatorTest {

    private final CorreoDuocValidator validator = new CorreoDuocValidator();

    @ParameterizedTest
    @ValueSource(strings = {"ke.gonzalezo@duocuc.cl", "ana@duoc.cl", "prof@profesor.duoc.cl", " KEVIN@DUOCUC.CL "})
    void aceptaCorreosInstitucionales(String correo) {
        assertTrue(validator.isValid(correo, null), correo);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alguien@gmail.com", "x@duocuc.com", "x@fakeduocuc.cl", "sin-arroba.cl"})
    void rechazaOtrosDominios(String correo) {
        assertFalse(validator.isValid(correo, null), correo);
    }

    @Test
    void nuloOVacioLoDejaALasOtrasAnotaciones() {
        assertTrue(validator.isValid(null, null));
        assertTrue(validator.isValid("", null));
    }
}
