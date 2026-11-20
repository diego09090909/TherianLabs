package cl.therionlabs.prospectos.controller;

import cl.therionlabs.prospectos.DatosPrueba;
import cl.therionlabs.prospectos.dto.EstadoResultado;
import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.dto.ResultadoProspecto;
import cl.therionlabs.prospectos.exception.ConflictoException;
import cl.therionlabs.prospectos.exception.RecursoNoEncontradoException;
import cl.therionlabs.prospectos.exception.SolicitudInvalidaException;
import cl.therionlabs.prospectos.service.ProspectoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProspectoController.class)
class ProspectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private ProspectoService service;

    @Test
    void listarResponde200() throws Exception {
        when(service.listar("EVT-1")).thenReturn(List.of(DatosPrueba.respuesta("EVT-1", "12345678-5")));

        mockMvc.perform(get("/api/prospectos").param("eventoId", "EVT-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rut").value("12345678-5"));
    }

    @Test
    void obtenerResponde200() throws Exception {
        when(service.obtener(1L)).thenReturn(DatosPrueba.respuesta("EVT-1", "12345678-5"));

        mockMvc.perform(get("/api/prospectos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Camila Andrea"));
    }

    @Test
    void obtenerInexistenteResponde404ConCuerpoDeError() throws Exception {
        when(service.obtener(9L)).thenThrow(new RecursoNoEncontradoException("Prospecto no encontrado"));

        mockMvc.perform(get("/api/prospectos/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Prospecto no encontrado"));
    }

    @Test
    void idConTextoResponde400() throws Exception {
        mockMvc.perform(get("/api/prospectos/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void crearResponde201() throws Exception {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12345678-5");
        when(service.crear(any(ProspectoRequest.class))).thenReturn(DatosPrueba.respuesta("EVT-1", "12345678-5"));

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rut").value("12345678-5"));
    }

    @Test
    void crearConRutInvalidoResponde400ConDetalle() throws Exception {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12345678-9");

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles[0]").value("rut: no es un RUT valido"));
    }

    @Test
    void crearConCorreoInvalidoResponde400() throws Exception {
        ProspectoRequest base = DatosPrueba.request("EVT-1", "12345678-5");
        ProspectoRequest request = new ProspectoRequest(
                base.eventoId(), base.captadorId(), base.rut(), base.nombres(), base.apellidos(), base.sexo(),
                base.fechaNacimiento(), base.comunaResidencia(), base.celular(), "no-es-un-correo", base.actividad(),
                base.actividadOtra(), base.establecimiento(), base.comunaEstablecimiento(), base.jornada(),
                base.sedes(), base.nivelInteres(), base.afirmacion(), base.carrera1(), base.carrera2(),
                base.carrera3(), base.autorizaContacto(), base.fechaCaptura());

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles[0]").value("email: no es un correo valido"));
    }

    @Test
    void crearConJsonRotoResponde400() throws Exception {
        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la solicitud no es un JSON valido"));
    }

    @Test
    void crearDuplicadoResponde409() throws Exception {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12345678-5");
        when(service.crear(any(ProspectoRequest.class))).thenThrow(new ConflictoException("Ese RUT ya esta registrado en el evento"));

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void loteDevuelveResultadoPorProspecto() throws Exception {
        RespuestaLote respuesta = new RespuestaLote(1, 1, 0, 0,
                List.of(new ResultadoProspecto("EVT-1", "12345678-5", EstadoResultado.CREADO, null, 7L)));
        when(service.procesarLote(any())).thenReturn(respuesta);
        List<ProspectoRequest> lote = List.of(DatosPrueba.request("EVT-1", "12345678-5"));

        mockMvc.perform(post("/api/prospectos/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(lote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creados").value(1))
                .andExpect(jsonPath("$.resultados[0].estado").value("CREADO"))
                .andExpect(jsonPath("$.resultados[0].id").value(7));
    }

    @Test
    void loteVacioResponde400() throws Exception {
        when(service.procesarLote(any())).thenThrow(new SolicitudInvalidaException("El lote esta vacio"));

        mockMvc.perform(post("/api/prospectos/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El lote esta vacio"));
    }

    @Test
    void actualizarResponde200() throws Exception {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12345678-5");
        when(service.actualizar(any(Long.class), any(ProspectoRequest.class)))
                .thenReturn(DatosPrueba.respuesta("EVT-1", "12345678-5"));

        mockMvc.perform(put("/api/prospectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarResponde204() throws Exception {
        mockMvc.perform(delete("/api/prospectos/1"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

    @Test
    void eliminarInexistenteResponde404() throws Exception {
        doThrow(new RecursoNoEncontradoException("Prospecto no encontrado")).when(service).eliminar(9L);

        mockMvc.perform(delete("/api/prospectos/9"))
                .andExpect(status().isNotFound());
    }
}
