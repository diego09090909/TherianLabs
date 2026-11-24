package cl.therionlabs.eventos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.therionlabs.eventos.dto.EventoActualizarRequest;
import cl.therionlabs.eventos.dto.EventoRequest;
import cl.therionlabs.eventos.dto.EventoResponse;
import cl.therionlabs.eventos.exception.ConflictoException;
import cl.therionlabs.eventos.exception.RecursoNoEncontradoException;
import cl.therionlabs.eventos.mapper.EventoMapper;
import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.model.Evento;
import cl.therionlabs.eventos.repository.EventoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository repository;

    private EventoService service;
    private final LocalDate fecha = LocalDate.of(2026, 10, 14);

    @BeforeEach
    void preparar() {
        service = new EventoService(repository, new EventoMapper());
    }

    private Evento evento(String id, EstadoEvento estado) {
        Evento e = new Evento();
        e.setId(id);
        e.setNombre("Feria");
        e.setLiceo("Liceo");
        e.setFecha(fecha);
        e.setEstado(estado);
        return e;
    }

    @Test
    void listarSinFiltroUsaFindAll() {
        when(repository.findAll()).thenReturn(List.of(evento("EVT-1", EstadoEvento.ABIERTO), evento("EVT-2", EstadoEvento.CERRADO)));

        assertEquals(2, service.listar(null).size());
    }

    @Test
    void listarConEstadoFiltraEnElRepositorio() {
        when(repository.findByEstado(EstadoEvento.ABIERTO)).thenReturn(List.of(evento("EVT-1", EstadoEvento.ABIERTO)));

        List<EventoResponse> r = service.listar(EstadoEvento.ABIERTO);

        assertEquals(1, r.size());
        assertEquals("EVT-1", r.get(0).id());
        verify(repository, never()).findAll();
    }

    @Test
    void obtenerNormalizaElCodigo() {
        when(repository.findById("EVT-0042")).thenReturn(Optional.of(evento("EVT-0042", EstadoEvento.ABIERTO)));

        assertEquals("EVT-0042", service.obtener(" evt-0042 ").id());
    }

    @Test
    void obtenerInexistenteLanzaNoEncontrado() {
        when(repository.findById("EVT-9999")).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class, () -> service.obtener("evt-9999"));
        assertEquals("Evento no encontrado: EVT-9999", ex.getMessage());
    }

    @Test
    void crearGuardaAbiertoYConIdEnMayusculas() {
        when(repository.existsById("EVT-7")).thenReturn(false);
        when(repository.save(any(Evento.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoResponse r = service.crear(new EventoRequest("evt-7", "Feria", "Liceo", fecha));

        ArgumentCaptor<Evento> cap = ArgumentCaptor.forClass(Evento.class);
        verify(repository).save(cap.capture());
        assertEquals("EVT-7", cap.getValue().getId());
        assertEquals(EstadoEvento.ABIERTO, r.estado());
    }

    @Test
    void crearConCodigoRepetidoLanzaConflicto() {
        when(repository.existsById("EVT-7")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.crear(new EventoRequest("EVT-7", "Feria", "Liceo", fecha)));
        verify(repository, never()).save(any());
    }

    @Test
    void actualizarCambiaLosDatosEditables() {
        Evento existente = evento("EVT-1", EstadoEvento.ABIERTO);
        when(repository.findById("EVT-1")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        EventoResponse r = service.actualizar("EVT-1", new EventoActualizarRequest("Nueva", "Otro", fecha.plusDays(7)));

        assertEquals("Nueva", r.nombre());
        assertEquals("Otro", r.liceo());
        assertEquals(fecha.plusDays(7), r.fecha());
    }

    @Test
    void actualizarInexistenteLanzaNoEncontrado() {
        when(repository.findById("EVT-1")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.actualizar("EVT-1", new EventoActualizarRequest("N", "L", fecha)));
    }

    @Test
    void cambiarEstadoCierraYAbre() {
        Evento existente = evento("EVT-1", EstadoEvento.ABIERTO);
        when(repository.findById("EVT-1")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        assertEquals(EstadoEvento.CERRADO, service.cambiarEstado("EVT-1", EstadoEvento.CERRADO).estado());
        assertEquals(EstadoEvento.ABIERTO, service.cambiarEstado("EVT-1", EstadoEvento.ABIERTO).estado());
    }

    @Test
    void eliminarBorraElEvento() {
        Evento existente = evento("EVT-1", EstadoEvento.ABIERTO);
        when(repository.findById("EVT-1")).thenReturn(Optional.of(existente));

        service.eliminar("evt-1");

        verify(repository).delete(existente);
    }

    @Test
    void eliminarInexistenteLanzaNoEncontrado() {
        when(repository.findById("EVT-1")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar("EVT-1"));
        verify(repository, never()).delete(any());
    }
}
