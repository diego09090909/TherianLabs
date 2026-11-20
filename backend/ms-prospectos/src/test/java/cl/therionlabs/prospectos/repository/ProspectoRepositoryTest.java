package cl.therionlabs.prospectos.repository;

import cl.therionlabs.prospectos.DatosPrueba;
import cl.therionlabs.prospectos.model.Prospecto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProspectoRepositoryTest {

    @Autowired
    private ProspectoRepository repository;

    @Test
    void guardaYBuscaPorEvento() {
        repository.save(DatosPrueba.prospecto("EVT-1", "12345678-5"));
        repository.save(DatosPrueba.prospecto("EVT-2", "12345678-5"));

        assertEquals(1, repository.findByEventoId("EVT-1").size());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void existsPorEventoYRut() {
        repository.save(DatosPrueba.prospecto("EVT-1", "12345678-5"));

        assertTrue(repository.existsByEventoIdAndRut("EVT-1", "12345678-5"));
        assertFalse(repository.existsByEventoIdAndRut("EVT-2", "12345678-5"));
        assertFalse(repository.existsByEventoIdAndRut("EVT-1", "11111111-1"));
    }

    @Test
    void noPermiteElMismoRutDosVecesEnUnEvento() {
        repository.saveAndFlush(DatosPrueba.prospecto("EVT-1", "12345678-5"));
        Prospecto repetido = DatosPrueba.prospecto("EVT-1", "12345678-5");

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(repetido));
    }
}
