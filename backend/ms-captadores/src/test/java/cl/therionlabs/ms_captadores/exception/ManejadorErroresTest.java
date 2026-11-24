package cl.therionlabs.ms_captadores.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class ManejadorErroresTest {

    private final ManejadorErrores manejador = new ManejadorErrores();

    void metodoDePrueba(String parametro) {
    }

    private void verificar(ResponseEntity<ErrorRespuesta> r, HttpStatus esperado, String mensaje) {
        assertEquals(esperado, r.getStatusCode());
        assertEquals(esperado.value(), r.getBody().status());
        assertEquals(esperado.getReasonPhrase(), r.getBody().error());
        assertEquals(mensaje, r.getBody().mensaje());
    }

    @Test
    void noEncontradoEs404() {
        verificar(manejador.noEncontrado(new RecursoNoEncontradoException("no está")), HttpStatus.NOT_FOUND, "no está");
    }

    @Test
    void conflictoEs409() {
        verificar(manejador.conflicto(new ConflictoException("repetido")), HttpStatus.CONFLICT, "repetido");
    }

    @Test
    void credencialesInvalidasEs401() {
        verificar(manejador.credenciales(new CredencialesInvalidasException("mal")), HttpStatus.UNAUTHORIZED, "mal");
    }

    @Test
    void accesoDenegadoEs403() {
        verificar(manejador.accesoDenegado(new AccesoDenegadoException("no")), HttpStatus.FORBIDDEN, "no");
    }

    @Test
    void validacionEs400ConDetallesOrdenados() throws Exception {
        Method m = ManejadorErroresTest.class.getDeclaredMethod("metodoDePrueba", String.class);
        var binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "rut", "El RUT no es válido"));
        binding.addError(new FieldError("request", "correo", "El correo es obligatorio"));

        var r = manejador.validacion(new MethodArgumentNotValidException(new MethodParameter(m, 0), binding));

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(List.of("correo: El correo es obligatorio", "rut: El RUT no es válido"), r.getBody().detalles());
    }

    @Test
    void jsonInvalidoEs400() {
        var ex = new HttpMessageNotReadableException("x", new MockHttpInputMessage(new byte[0]));

        verificar(manejador.jsonInvalido(ex), HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es un JSON válido");
    }

    @Test
    void tipoInvalidoEs400ConElNombreDelParametro() throws Exception {
        Method m = ManejadorErroresTest.class.getDeclaredMethod("metodoDePrueba", String.class);
        var ex = new MethodArgumentTypeMismatchException("abc", Long.class, "id", new MethodParameter(m, 0), null);

        var r = manejador.tipoInvalido(ex);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertTrue(r.getBody().mensaje().contains("'id'"));
    }

    @Test
    void errorInesperadoEs500SinFiltrarDetalles() {
        var r = manejador.inesperado(new RuntimeException("secreto interno"));

        verificar(r, HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno");
    }
}
