package cl.therionlabs.ms_captadores.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.therionlabs.ms_captadores.dto.ActualizarCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.AuthResponse;
import cl.therionlabs.ms_captadores.dto.CaptadorResponse;
import cl.therionlabs.ms_captadores.dto.LoginCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import cl.therionlabs.ms_captadores.mapper.CaptadorMapper;
import cl.therionlabs.ms_captadores.model.Captador;
import cl.therionlabs.ms_captadores.service.CaptadorService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CaptadorControllerTest {

    @Mock
    private CaptadorService service;

    private final Captador ana = new Captador(1L, "Ana", "12345678-5", "ana@duocuc.cl", "987654321", "h");
    private final CaptadorResponse resp = new CaptadorResponse(1L, "Ana", "12345678-5", "ana@duocuc.cl", "987654321");

    private CaptadorController controlador() {
        return new CaptadorController(service, new CaptadorMapper());
    }

    @Test
    void registrarResponde201() {
        var req = new RegistroCaptadorRequest("Ana", "12345678-5", "ana@duocuc.cl", "987654321", "clave123");
        var auth = new AuthResponse("tok", "Bearer", resp);
        when(service.registrar(req)).thenReturn(auth);

        var r = controlador().registrar(req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertSame(auth, r.getBody());
    }

    @Test
    void loginDevuelveLaRespuestaDelServicio() {
        var req = new LoginCaptadorRequest("ana@duocuc.cl", "clave123");
        var auth = new AuthResponse("tok", "Bearer", resp);
        when(service.login(req)).thenReturn(auth);

        assertSame(auth, controlador().login(req));
    }

    @Test
    void listarYObtenerDelegan() {
        when(service.listar()).thenReturn(List.of(resp));
        when(service.obtener(1L)).thenReturn(resp);

        assertEquals(1, controlador().listar().size());
        assertSame(resp, controlador().obtener(1L));
    }

    @Test
    void yoDevuelveElCaptadorAutenticado() {
        CaptadorResponse r = controlador().yo(ana);

        assertEquals("ana@duocuc.cl", r.getCorreo());
    }

    @Test
    void actualizarPasaAlSolicitante() {
        var req = new ActualizarCaptadorRequest("Ana M", "911111111");
        when(service.actualizar(1L, req, ana)).thenReturn(resp);

        assertSame(resp, controlador().actualizar(1L, req, ana));
    }

    @Test
    void eliminarResponde204() {
        var r = controlador().eliminar(1L, ana);

        verify(service).eliminar(1L, ana);
        assertEquals(HttpStatus.NO_CONTENT, r.getStatusCode());
    }
}
