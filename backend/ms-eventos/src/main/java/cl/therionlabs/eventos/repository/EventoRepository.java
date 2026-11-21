package cl.therionlabs.eventos.repository;

import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, String> {

    List<Evento> findByEstado(EstadoEvento estado);
}
