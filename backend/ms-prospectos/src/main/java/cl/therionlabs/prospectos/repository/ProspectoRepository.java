package cl.therionlabs.prospectos.repository;

import cl.therionlabs.prospectos.model.Prospecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProspectoRepository extends JpaRepository<Prospecto, Long> {
    // >> QUE HACE: Al extender JpaRepository ya tienes gratis save, findById, findAll, deleteById. No escribes SQL.

    boolean existsByEventoIdAndRut(Long eventoId, String rut);
    // >> QUE HACE: Pregunta a la base "existe un prospecto con este evento y este RUT?". Spring arma la consulta por el nombre del metodo.

    List<Prospecto> findByEventoId(Long eventoId);
    // >> QUE HACE: Lista los prospectos de un evento.
}
