package cl.therionlabs.ms_captadores.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.therionlabs.ms_captadores.dto.ActualizarCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.LoginCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class RegistroCaptadorRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<String> campos(Object objeto) {
        return validator.validate(objeto).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    void registroValidoNoTieneErrores() {
        var req = new RegistroCaptadorRequest("Kevin Gonzalez", "12345678-5", "ke.gonzalezo@duocuc.cl", "987654321", "clave123");
        assertTrue(validator.validate(req).isEmpty());
    }

    @Test
    void registroVacioMarcaTodosLosCampos() {
        assertEquals(Set.of("nombre", "rut", "correo", "telefono", "contrasena"), campos(new RegistroCaptadorRequest()));
    }

    @Test
    void registroConDatosMalosSenalaCadaCampo() {
        var req = new RegistroCaptadorRequest("Kevin", "12345678-4", "kevin@gmail.com", "123", "abc");
        assertEquals(Set.of("rut", "correo", "telefono", "contrasena"), campos(req));
    }

    @Test
    void correoNoDuocTraeMensajeInstitucional() {
        var req = new RegistroCaptadorRequest("Kevin", "12345678-5", "kevin@gmail.com", "987654321", "clave123");
        Set<ConstraintViolation<RegistroCaptadorRequest>> errores = validator.validate(req);
        assertEquals("Debe ingresar un correo institucional de Duoc UC", errores.iterator().next().getMessage());
    }

    @Test
    void loginExigeCorreoValidoYContrasena() {
        assertEquals(Set.of("correo", "contrasena"), campos(new LoginCaptadorRequest()));
        assertEquals(Set.of("correo"), campos(new LoginCaptadorRequest("no-es-correo", "x")));
        assertTrue(campos(new LoginCaptadorRequest("a@duocuc.cl", "x")).isEmpty());
    }

    @Test
    void actualizarExigeNombreYTelefono() {
        assertEquals(Set.of("nombre", "telefono"), campos(new ActualizarCaptadorRequest()));
        assertEquals(Set.of("telefono"), campos(new ActualizarCaptadorRequest("Ana", "1")));
        assertTrue(campos(new ActualizarCaptadorRequest("Ana", "987654321")).isEmpty());
    }
}
