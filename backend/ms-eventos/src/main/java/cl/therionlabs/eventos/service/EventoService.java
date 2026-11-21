package cl.therionlabs.eventos.service;

import cl.therionlabs.eventos.dto.EventoActualizarRequest;
import cl.therionlabs.eventos.dto.EventoRequest;
import cl.therionlabs.eventos.dto.EventoResponse;
import cl.therionlabs.eventos.exception.ConflictoException;
import cl.therionlabs.eventos.exception.RecursoNoEncontradoException;
import cl.therionlabs.eventos.mapper.EventoMapper;
import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.model.Evento;
import cl.therionlabs.eventos.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;
    private final EventoMapper mapper;

    public EventoService(EventoRepository repository, EventoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EventoResponse> listar(EstadoEvento estado) {
        List<Evento> eventos = (estado != null)
                ? repository.findByEstado(estado)
                : repository.findAll();
        return eventos.stream().map(mapper::aRespuesta).toList();
    }

    public EventoResponse obtener(String id) {
        return mapper.aRespuesta(buscar(id));
    }

    @Transactional
    public EventoResponse crear(EventoRequest request) {
        Evento evento = mapper.aEntidad(request);
        if (repository.existsById(evento.getId())) {
            throw new ConflictoException("Ya existe un evento con el codigo " + evento.getId());
        }
        return mapper.aRespuesta(repository.save(evento));
    }

    @Transactional
    public EventoResponse actualizar(String id, EventoActualizarRequest request) {
        Evento evento = buscar(id);
        mapper.actualizar(evento, request);
        return mapper.aRespuesta(repository.save(evento));
    }

    @Transactional
    public EventoResponse cambiarEstado(String id, EstadoEvento nuevoEstado) {
        Evento evento = buscar(id);
        evento.setEstado(nuevoEstado);
        return mapper.aRespuesta(repository.save(evento));
    }

    @Transactional
    public void eliminar(String id) {
        Evento evento = buscar(id);
        repository.delete(evento);
    }

    private Evento buscar(String id) {
        String codigo = EventoMapper.normalizarId(id);
        return repository.findById(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado: " + codigo));
    }
}
