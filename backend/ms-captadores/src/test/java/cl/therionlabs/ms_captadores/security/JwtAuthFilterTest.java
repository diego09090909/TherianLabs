package cl.therionlabs.ms_captadores.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cl.therionlabs.ms_captadores.model.Captador;
import cl.therionlabs.ms_captadores.repository.CaptadorRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private CaptadorRepository repository;
    private JwtAuthFilter filtro;
    private Captador captador;

    @BeforeEach
    void preparar() {
        SecurityContextHolder.clearContext();
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "VGhlcmlhbkxhYnNDYXB0YWRvcmVzU2VjcmV0S2V5MjAyNkR1b2NVQ0FwcE1vdmls");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 60_000L);
        repository = mock(CaptadorRepository.class);
        filtro = new JwtAuthFilter(jwtService, repository);
        captador = new Captador(5L, "Ana", "12345678-5", "ana@duocuc.cl", "987654321", "hash");
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private MockFilterChain ejecutar(String cabecera) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (cabecera != null) {
            request.addHeader("Authorization", cabecera);
        }
        MockFilterChain cadena = new MockFilterChain();
        filtro.doFilter(request, new MockHttpServletResponse(), cadena);
        return cadena;
    }

    @Test
    void sinCabeceraContinuaSinAutenticar() throws Exception {
        MockFilterChain cadena = ejecutar(null);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }

    @Test
    void cabeceraQueNoEsBearerSeIgnora() throws Exception {
        ejecutar("Basic abc");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenValidoAutenticaAlCaptador() throws Exception {
        String token = jwtService.generarToken(captador);
        when(repository.findByCorreo("ana@duocuc.cl")).thenReturn(Optional.of(captador));

        ejecutar("Bearer " + token);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(captador, auth.getPrincipal());
    }

    @Test
    void tokenDeUsuarioInexistenteNoAutentica() throws Exception {
        String token = jwtService.generarToken(captador);
        when(repository.findByCorreo("ana@duocuc.cl")).thenReturn(Optional.empty());

        ejecutar("Bearer " + token);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenBasuraNoAutenticaNiRompeLaCadena() throws Exception {
        MockFilterChain cadena = ejecutar("Bearer esto.no.es.un.jwt");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }
}
