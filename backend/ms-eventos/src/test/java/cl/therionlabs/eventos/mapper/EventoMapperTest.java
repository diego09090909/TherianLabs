package cl.therionlabs.eventos.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import cl.therionlabs.eventos.dto.EventoActualizarRequest;
import cl.therionlabs.eventos.dto.EventoRequest;
import cl.therionlabs.eventos.dto.EventoResponse;
import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.model.Evento;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EventoMapperTest {

    private final EventoMapper mapper = new EventoMapper();
    private final LocalDate fecha = LocalDate.of(2026, 10, 14);

    @Test
    void aEntidadNormalizaElIdYQuedaAbierto() {
        Evento e = mapper.aEntidad(new EventoRequest(" evt-0042 ", " Feria Vocacional ", " Liceo Bicentenario ", fecha));

        assertEquals("EVT-0042", e.getId());
        assertEquals("Feria Vocacional", e.getNombre());
        assertEquals("Liceo Bicentenario", e.getLiceo());
        assertEquals(fecha, e.getFecha());
        assertEquals(EstadoEvento.ABIERTO, e.getEstado());
    }

    @Test
    void actualizarCambiaNombreLiceoYFechaPeroNoIdNiEstado() {
        Evento e = new Evento();
        e.setId("EVT-1");
        e.setEstado(EstadoEvento.CERRADO);

        mapper.actualizar(e, new EventoActualizarRequest(" Nuevo ", " Otro liceo ", fecha));

        assertEquals("Nuevo", e.getNombre());
        assertEquals("Otro liceo", e.getLiceo());
        assertEquals(fecha, e.getFecha());
        assertEquals("EVT-1", e.getId());
        assertEquals(EstadoEvento.CERRADO, e.getEstado());
    }

    @Test
    void aRespuestaCopiaTodosLosCampos() {
        Evento e = new Evento();
        e.setId("EVT-1");
        e.setNombre("Feria");
        e.setLiceo("Liceo");
        e.setFecha(fecha);
        e.setEstado(EstadoEvento.CERRADO);

        EventoResponse r = mapper.aRespuesta(e);

        assertEquals(new EventoResponse("EVT-1", "Feria", "Liceo", fecha, EstadoEvento.CERRADO), r);
    }

    @Test
    void normalizarIdAceptaNulo() {
        assertNull(EventoMapper.normalizarId(null));
        assertEquals("ABC-1", EventoMapper.normalizarId(" abc-1 "));
    }
}
