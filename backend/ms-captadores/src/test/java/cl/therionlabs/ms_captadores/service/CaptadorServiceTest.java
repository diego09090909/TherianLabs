package cl.therionlabs.ms_captadores.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.therionlabs.ms_captadores.dto.ActualizarCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.AuthResponse;
import cl.therionlabs.ms_captadores.dto.CambiarContrasenaRequest;
import cl.therionlabs.ms_captadores.dto.CaptadorResponse;
import cl.therionlabs.ms_captadores.dto.LoginCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import cl.therionlabs.ms_captadores.exception.AccesoDenegadoException;
import cl.therionlabs.ms_captadores.exception.ConflictoException;
import cl.therionlabs.ms_captadores.exception.CredencialesInvalidasException;
import cl.therionlabs.ms_captadores.exception.RecursoNoEncontradoException;
import cl.therionlabs.ms_captadores.mapper.CaptadorMapper;
import cl.therionlabs.ms_captadores.model.Captador;
import cl.therionlabs.ms_captadores.repository.CaptadorRepository;
import cl.therionlabs.ms_captadores.security.JwtService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CaptadorServiceTest {

    @Mock
    private CaptadorRepository repository;
    @Mock
    private JwtService jwtService;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private CaptadorService service;
    private Captador ana;

    @BeforeEach
    void preparar() {
        service = new CaptadorService(repository, new CaptadorMapper(), encoder, jwtService);
        ana = new Captador(1L, "Ana", "12345678-5", "ana@duocuc.cl", "987654321", encoder.encode("clave123"));
    }

    private RegistroCaptadorRequest registro() {
        return new RegistroCaptadorRequest("Ana", "12.345.678-5", "ANA@duocuc.cl", "987654321", "clave123");
    }

    @Test
    void registrarGuardaConContrasenaEncriptadaYDevuelveToken() {
        when(repository.existsByCorreo("ana@duocuc.cl")).thenReturn(false);
        when(repository.existsByRut("12345678-5")).thenReturn(false);
        when(repository.save(any(Captador.class))).thenAnswer(inv -> {
            Captador c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(jwtService.generarToken(any(Captador.class))).thenReturn("tok");

        AuthResponse r = service.registrar(registro());

        ArgumentCaptor<Captador> cap = ArgumentCaptor.forClass(Captador.class);
        verify(repository).save(cap.capture());
        assertNotEquals("clave123", cap.getValue().getContrasena());
        assertEquals(true, encoder.matches("clave123", cap.getValue().getContrasena()));
        assertEquals("tok", r.getToken());
        assertEquals("Bearer", r.getTipo());
        assertEquals("ana@duocuc.cl", r.getCaptador().getCorreo());
    }

    @Test
    void registrarConCorreoRepetidoLanzaConflicto() {
        when(repository.existsByCorreo("ana@duocuc.cl")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.registrar(registro()));
        verify(repository, never()).save(any());
    }

    @Test
    void registrarConRutRepetidoLanzaConflicto() {
        when(repository.existsByCorreo("ana@duocuc.cl")).thenReturn(false);
        when(repository.existsByRut("12345678-5")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.registrar(registro()));
        verify(repository, never()).save(any());
    }

    @Test
    void loginCorrectoDevuelveToken() {
        when(repository.findByCorreo("ana@duocuc.cl")).thenReturn(Optional.of(ana));
        when(jwtService.generarToken(ana)).thenReturn("tok");

        AuthResponse r = service.login(new LoginCaptadorRequest(" Ana@duocuc.cl ", "clave123"));

        assertEquals("tok", r.getToken());
        assertEquals(1L, r.getCaptador().getId());
    }

    @Test
    void loginConContrasenaIncorrectaFalla() {
        when(repository.findByCorreo("ana@duocuc.cl")).thenReturn(Optional.of(ana));

        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginCaptadorRequest("ana@duocuc.cl", "otra")));
    }

    @Test
    void loginConCorreoInexistenteFalla() {
        when(repository.findByCorreo("nadie@duocuc.cl")).thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginCaptadorRequest("nadie@duocuc.cl", "x")));
    }

    @Test
    void listarDevuelveRespuestasSinContrasena() {
        when(repository.findAll()).thenReturn(List.of(ana));

        List<CaptadorResponse> lista = service.listar();

        assertEquals(1, lista.size());
        assertEquals("ana@duocuc.cl", lista.get(0).getCorreo());
    }

    @Test
    void obtenerExistenteYNoExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(ana));
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertEquals("Ana", service.obtener(1L).getNombre());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(9L));
    }

    @Test
    void actualizarPropiaCuentaCambiaNombreYTelefono() {
        when(repository.findById(1L)).thenReturn(Optional.of(ana));
        when(repository.save(ana)).thenReturn(ana);

        CaptadorResponse r = service.actualizar(1L, new ActualizarCaptadorRequest("Ana Maria", "911111111"), ana);

        assertEquals("Ana Maria", r.getNombre());
        assertEquals("911111111", r.getTelefono());
    }

    @Test
    void actualizarCuentaAjenaOSinSesionEsProhibido() {
        Captador otro = new Captador(2L, "Beto", "11111111-1", "beto@duocuc.cl", "922222222", "h");
        var req = new ActualizarCaptadorRequest("X", "933333333");

        assertThrows(AccesoDenegadoException.class, () -> service.actualizar(1L, req, otro));
        assertThrows(AccesoDenegadoException.class, () -> service.actualizar(1L, req, null));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarPropiaCuentaBorra() {
        when(repository.findById(1L)).thenReturn(Optional.of(ana));

        service.eliminar(1L, ana);

        verify(repository).delete(ana);
    }

    @Test
    void eliminarCuentaAjenaEsProhibidoYNoExistenteFalla() {
        Captador otro = new Captador(2L, "Beto", "11111111-1", "beto@duocuc.cl", "922222222", "h");
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AccesoDenegadoException.class, () -> service.eliminar(1L, otro));
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(2L, otro));
        verify(repository, never()).delete(any());
    }

    @Test
    void cambiarContrasenaConActualCorrectaLaReemplaza() {
        when(repository.findById(1L)).thenReturn(Optional.of(ana));

        service.cambiarContrasena(1L, new CambiarContrasenaRequest("clave123", "nueva456"), ana);

        verify(repository).save(ana);
        assertEquals(true, encoder.matches("nueva456", ana.getContrasena()));
    }

    @Test
    void cambiarContrasenaConActualIncorrectaFalla() {
        when(repository.findById(1L)).thenReturn(Optional.of(ana));

        assertThrows(CredencialesInvalidasException.class,
                () -> service.cambiarContrasena(1L, new CambiarContrasenaRequest("otra", "nueva456"), ana));
        verify(repository, never()).save(any());
    }
}
