package cl.therionlabs.ms_captadores.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.therionlabs.ms_captadores.model.Captador;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private static final String SECRETO = "VGhlcmlhbkxhYnNDYXB0YWRvcmVzU2VjcmV0S2V5MjAyNkR1b2NVQ0FwcE1vdmls";

    private JwtService jwtService;
    private Captador captador;

    @BeforeEach
    void preparar() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRETO);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
        captador = new Captador(5L, "Ana", "12345678-5", "ana@duocuc.cl", "987654321", "hash");
    }

    @Test
    void tokenGeneradoGuardaElCorreoYLosDatos() {
        String token = jwtService.generarToken(captador);

        assertEquals("ana@duocuc.cl", jwtService.extraerCorreo(token));
        assertEquals("Ana", jwtService.extraerClaim(token, c -> c.get("nombre", String.class)));
        assertEquals("12345678-5", jwtService.extraerClaim(token, c -> c.get("rut", String.class)));
        assertEquals(5, jwtService.extraerClaim(token, c -> c.get("id", Integer.class)));
    }

    @Test
    void tokenEsValidoSoloParaSuCorreo() {
        String token = jwtService.generarToken(captador);

        assertTrue(jwtService.esTokenValido(token, "ana@duocuc.cl"));
        assertFalse(jwtService.esTokenValido(token, "otro@duocuc.cl"));
    }

    @Test
    void tokenExpiradoLanzaExcepcion() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1_000L);
        String token = jwtService.generarToken(captador);

        assertThrows(JwtException.class, () -> jwtService.esTokenValido(token, "ana@duocuc.cl"));
    }

    @Test
    void tokenAdulteradoLanzaExcepcion() {
        String token = jwtService.generarToken(captador);
        String adulterado = token.substring(0, token.length() - 3) + "abc";

        assertThrows(JwtException.class, () -> jwtService.extraerCorreo(adulterado));
    }

    @Test
    void tokenFirmadoConOtraLlaveSeRechaza() {
        String token = jwtService.generarToken(captador);
        JwtService otro = new JwtService();
        ReflectionTestUtils.setField(otro, "secretKey", "b3RyYUxsYXZlQ29tcGxldGFtZW50ZURpc3RpbnRhUGFyYVByb2JhcjEyMzQ1Ng==");
        ReflectionTestUtils.setField(otro, "jwtExpiration", 60_000L);

        assertThrows(JwtException.class, () -> otro.extraerCorreo(token));
    }
}
