package cl.therionlabs.prospectos.controller;

import cl.therionlabs.prospectos.DatosPrueba;
import cl.therionlabs.prospectos.dto.EstadoResultado;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.dto.ResultadoProspecto;
import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.service.ProspectoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

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
        when(service.listar("EVT-1")).thenReturn(List.of(DatosPrueba.prospecto("EVT-1", "12345678-5")));

        mockMvc.perform(get("/api/prospectos").param("eventoId", "EVT-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rut").value("12345678-5"));
    }

    @Test
    void obtenerResponde200() throws Exception {
        when(service.obtener(1L)).thenReturn(DatosPrueba.prospecto("EVT-1", "12345678-5"));

        mockMvc.perform(get("/api/prospectos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Camila Andrea"));
    }

    @Test
    void obtenerInexistenteResponde404() throws Exception {
        when(service.obtener(9L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/prospectos/9"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearResponde201() throws Exception {
        Prospecto p = DatosPrueba.prospecto("EVT-1", "12345678-5");
        when(service.crear(any(Prospecto.class))).thenReturn(p);

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rut").value("12345678-5"));
    }

    @Test
    void crearSinCamposObligatoriosResponde400() throws Exception {
        Prospecto p = DatosPrueba.prospecto("EVT-1", "12345678-5");
        p.setEmail("no-es-un-correo");

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(p)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearDuplicadoResponde409() throws Exception {
        Prospecto p = DatosPrueba.prospecto("EVT-1", "12345678-5");
        when(service.crear(any(Prospecto.class))).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        mockMvc.perform(post("/api/prospectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(p)))
                .andExpect(status().isConflict());
    }

    @Test
    void loteDevuelveResultadoPorProspecto() throws Exception {
        RespuestaLote respuesta = new RespuestaLote(1, 1, 0, 0,
                List.of(new ResultadoProspecto("EVT-1", "12345678-5", EstadoResultado.CREADO, null, 7L)));
        when(service.procesarLote(any())).thenReturn(respuesta);
        List<Prospecto> lote = List.of(DatosPrueba.prospecto("EVT-1", "12345678-5"));

        mockMvc.perform(post("/api/prospectos/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(lote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creados").value(1))
                .andExpect(jsonPath("$.resultados[0].estado").value("CREADO"))
                .andExpect(jsonPath("$.resultados[0].id").value(7));
    }

    @Test
    void actualizarResponde200() throws Exception {
        Prospecto p = DatosPrueba.prospecto("EVT-1", "12345678-5");
        when(service.actualizar(any(Long.class), any(Prospecto.class))).thenReturn(p);

        mockMvc.perform(put("/api/prospectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(p)))
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
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(service).eliminar(9L);

        mockMvc.perform(delete("/api/prospectos/9"))
                .andExpect(status().isNotFound());
    }
}
