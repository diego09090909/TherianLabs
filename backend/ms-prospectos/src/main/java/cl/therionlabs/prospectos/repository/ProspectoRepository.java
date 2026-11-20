package cl.therionlabs.prospectos.repository;

import cl.therionlabs.prospectos.model.Prospecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProspectoRepository extends JpaRepository<Prospecto, Long> {

    boolean existsByEventoIdAndRut(String eventoId, String rut);

    List<Prospecto> findByEventoId(String eventoId);
}
