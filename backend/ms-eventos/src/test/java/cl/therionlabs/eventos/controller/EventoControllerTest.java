package cl.therionlabs.eventos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.therionlabs.eventos.dto.EventoActualizarRequest;
import cl.therionlabs.eventos.dto.EventoRequest;
import cl.therionlabs.eventos.dto.EventoResponse;
import cl.therionlabs.eventos.exception.ConflictoException;
import cl.therionlabs.eventos.exception.RecursoNoEncontradoException;
import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.service.EventoService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventoController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EventoService service;

    private final LocalDate fecha = LocalDate.of(2026, 10, 14);
    private final EventoResponse respuesta = new EventoResponse("EVT-0042", "Feria Vocacional", "Liceo Bicentenario", fecha, EstadoEvento.ABIERTO);

    private static final String CUERPO_VALIDO =
            "{\"id\":\"EVT-0042\",\"nombre\":\"Feria Vocacional\",\"liceo\":\"Liceo Bicentenario\",\"fecha\":\"2026-10-14\"}";

    @Test
    void listarDevuelveTodos() throws Exception {
        when(service.listar(null)).thenReturn(List.of(respuesta));

        mvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("EVT-0042"))
                .andExpect(jsonPath("$[0].fecha").value("2026-10-14"))
                .andExpect(jsonPath("$[0].estado").value("ABIERTO"));
    }

    @Test
    void listarPuedeFiltrarPorEstado() throws Exception {
        when(service.listar(EstadoEvento.CERRADO)).thenReturn(List.of());

        mvc.perform(get("/api/eventos").param("estado", "CERRADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listarConEstadoInvalidoDa400() throws Exception {
        mvc.perform(get("/api/eventos").param("estado", "XYZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El parametro 'estado' tiene un valor invalido"));
    }

    @Test
    void obtenerExistente() throws Exception {
        when(service.obtener("EVT-0042")).thenReturn(respuesta);

        mvc.perform(get("/api/eventos/EVT-0042"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liceo").value("Liceo Bicentenario"));
    }

    @Test
    void obtenerInexistenteDa404() throws Exception {
        when(service.obtener("EVT-9999")).thenThrow(new RecursoNoEncontradoException("Evento no encontrado: EVT-9999"));

        mvc.perform(get("/api/eventos/EVT-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Evento no encontrado: EVT-9999"));
    }

    @Test
    void crearDevuelve201() throws Exception {
        when(service.crear(any(EventoRequest.class))).thenReturn(respuesta);

        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(CUERPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("EVT-0042"));
    }

    @Test
    void crearConCodigoRepetidoDa409() throws Exception {
        when(service.crear(any(EventoRequest.class))).thenThrow(new ConflictoException("Ya existe un evento con el codigo EVT-0042"));

        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(CUERPO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void crearSinCamposDa400ConDetalles() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.length()").value(4))
                .andExpect(jsonPath("$.detalles[0]").value("fecha: es obligatorio"));

        verify(service, never()).crear(any());
    }

    @Test
    void crearConCodigoConCaracteresInvalidosDa400() throws Exception {
        String cuerpo = "{\"id\":\"EVT 42!\",\"nombre\":\"F\",\"liceo\":\"L\",\"fecha\":\"2026-10-14\"}";

        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles[0]").value("id: solo puede tener letras, numeros y guiones"));
    }

    @Test
    void crearConCodigoMuyLargoDa400() throws Exception {
        String cuerpo = "{\"id\":\"EVT-" + "1".repeat(20) + "\",\"nombre\":\"F\",\"liceo\":\"L\",\"fecha\":\"2026-10-14\"}";

        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles[0]").value("id: no puede superar 20 caracteres"));
    }

    @Test
    void jsonRotoDa400() throws Exception {
        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content("esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la solicitud no es un JSON valido"));
    }

    @Test
    void fechaConFormatoInvalidoDa400() throws Exception {
        String cuerpo = "{\"id\":\"EVT-1\",\"nombre\":\"F\",\"liceo\":\"L\",\"fecha\":\"14/10/2026\"}";

        mvc.perform(post("/api/eventos").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarDevuelve200() throws Exception {
        when(service.actualizar(any(String.class), any(EventoActualizarRequest.class))).thenReturn(respuesta);

        mvc.perform(put("/api/eventos/EVT-0042").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Feria\",\"liceo\":\"Liceo\",\"fecha\":\"2026-10-20\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("EVT-0042"));
    }

    @Test
    void actualizarSinDatosDa400() throws Exception {
        mvc.perform(put("/api/eventos/EVT-0042").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.length()").value(3));
    }

    @Test
    void cerrarYAbrirCambianElEstado() throws Exception {
        when(service.cambiarEstado("EVT-0042", EstadoEvento.CERRADO))
                .thenReturn(new EventoResponse("EVT-0042", "F", "L", fecha, EstadoEvento.CERRADO));
        when(service.cambiarEstado("EVT-0042", EstadoEvento.ABIERTO)).thenReturn(respuesta);

        mvc.perform(patch("/api/eventos/EVT-0042/cerrar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADO"));
        mvc.perform(patch("/api/eventos/EVT-0042/abrir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ABIERTO"));
    }

    @Test
    void eliminarDevuelve204() throws Exception {
        mvc.perform(delete("/api/eventos/EVT-0042")).andExpect(status().isNoContent());

        verify(service).eliminar("EVT-0042");
    }

    @Test
    void eliminarInexistenteDa404() throws Exception {
        org.mockito.Mockito.doThrow(new RecursoNoEncontradoException("Evento no encontrado: EVT-1"))
                .when(service).eliminar("EVT-1");

        mvc.perform(delete("/api/eventos/EVT-1")).andExpect(status().isNotFound());
    }
}
