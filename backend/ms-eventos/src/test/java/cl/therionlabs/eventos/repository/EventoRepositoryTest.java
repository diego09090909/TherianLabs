package cl.therionlabs.eventos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.model.Evento;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class EventoRepositoryTest {

    @Autowired
    private EventoRepository repository;

    private Evento evento(String id, EstadoEvento estado) {
        Evento e = new Evento();
        e.setId(id);
        e.setNombre("Feria " + id);
        e.setLiceo("Liceo");
        e.setFecha(LocalDate.of(2026, 10, 14));
        e.setEstado(estado);
        return e;
    }

    @Test
    void guardaYBuscaPorId() {
        repository.save(evento("EVT-1", EstadoEvento.ABIERTO));

        Evento e = repository.findById("EVT-1").orElseThrow();

        assertEquals("Feria EVT-1", e.getNombre());
        assertEquals(LocalDate.of(2026, 10, 14), e.getFecha());
        assertTrue(repository.existsById("EVT-1"));
    }

    @Test
    void findByEstadoDevuelveSoloLosDeEseEstado() {
        repository.save(evento("EVT-1", EstadoEvento.ABIERTO));
        repository.save(evento("EVT-2", EstadoEvento.CERRADO));
        repository.save(evento("EVT-3", EstadoEvento.ABIERTO));

        List<Evento> abiertos = repository.findByEstado(EstadoEvento.ABIERTO);
        List<Evento> cerrados = repository.findByEstado(EstadoEvento.CERRADO);

        assertEquals(2, abiertos.size());
        assertEquals(1, cerrados.size());
        assertEquals("EVT-2", cerrados.get(0).getId());
    }
}
