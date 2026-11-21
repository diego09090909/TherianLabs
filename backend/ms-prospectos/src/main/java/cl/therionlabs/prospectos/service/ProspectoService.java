package cl.therionlabs.prospectos.service;

import cl.therionlabs.prospectos.client.EventoCliente;
import cl.therionlabs.prospectos.client.EventoInfo;
import cl.therionlabs.prospectos.dto.EstadoResultado;
import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.ProspectoResponse;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.dto.ResultadoProspecto;
import cl.therionlabs.prospectos.exception.ConflictoException;
import cl.therionlabs.prospectos.exception.RecursoNoEncontradoException;
import cl.therionlabs.prospectos.exception.SolicitudInvalidaException;
import cl.therionlabs.prospectos.mapper.ProspectoMapper;
import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.repository.ProspectoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProspectoService {

    static final int MAX_LOTE = 500;
    private static final String MSG_DUPLICADO = "Ese RUT ya esta registrado en el evento";

    private final ProspectoRepository repository;
    private final ProspectoMapper mapper;
    private final Validator validator;
    private final EventoCliente eventoCliente;

    public ProspectoService(ProspectoRepository repository, ProspectoMapper mapper, Validator validator,
                            EventoCliente eventoCliente) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.eventoCliente = eventoCliente;
    }

    public List<ProspectoResponse> listar(String eventoId) {
        List<Prospecto> prospectos = (eventoId != null && !eventoId.isBlank())
                ? repository.findByEventoId(eventoId)
                : repository.findAll();
        return prospectos.stream().map(mapper::aRespuesta).toList();
    }

    public ProspectoResponse obtener(Long id) {
        return mapper.aRespuesta(buscar(id));
    }

    public ProspectoResponse crear(ProspectoRequest request) {
        Prospecto prospecto = mapper.aEntidad(request);
        String problemaEvento = revisarEvento(prospecto.getEventoId(), new HashMap<>());
        if (problemaEvento != null) {
            throw new SolicitudInvalidaException(problemaEvento);
        }
        if (repository.existsByEventoIdAndRut(prospecto.getEventoId(), prospecto.getRut())) {
            throw new ConflictoException(MSG_DUPLICADO);
        }
        return mapper.aRespuesta(repository.save(prospecto));
    }

    public RespuestaLote procesarLote(List<ProspectoRequest> lote) {
        if (lote == null || lote.isEmpty()) {
            throw new SolicitudInvalidaException("El lote esta vacio");
        }
        if (lote.size() > MAX_LOTE) {
            throw new SolicitudInvalidaException("El lote no puede superar " + MAX_LOTE + " prospectos");
        }
        List<ResultadoProspecto> resultados = new ArrayList<>();
        Map<String, String> eventosRevisados = new HashMap<>();
        for (ProspectoRequest request : lote) {
            resultados.add(procesarUno(request, eventosRevisados));
        }
        return new RespuestaLote(
                resultados.size(),
                contar(resultados, EstadoResultado.CREADO),
                contar(resultados, EstadoResultado.DUPLICADO),
                contar(resultados, EstadoResultado.INVALIDO),
                resultados
        );
    }

    public ProspectoResponse actualizar(Long id, ProspectoRequest request) {
        Prospecto actual = buscar(id);
        mapper.actualizar(actual, request);
        return mapper.aRespuesta(repository.save(actual));
    }

    public void eliminar(Long id) {
        buscar(id);
        repository.deleteById(id);
    }

    private Prospecto buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prospecto no encontrado"));
    }

    private ResultadoProspecto procesarUno(ProspectoRequest request, Map<String, String> eventosRevisados) {
        if (request == null) {
            return new ResultadoProspecto(null, null, EstadoResultado.INVALIDO, "Prospecto vacio", null);
        }
        String error = revisar(request);
        if (error != null) {
            return new ResultadoProspecto(request.eventoId(), request.rut(), EstadoResultado.INVALIDO, error, null);
        }
        Prospecto prospecto = mapper.aEntidad(request);
        String problemaEvento = revisarEvento(prospecto.getEventoId(), eventosRevisados);
        if (problemaEvento != null) {
            return new ResultadoProspecto(prospecto.getEventoId(), prospecto.getRut(), EstadoResultado.INVALIDO, problemaEvento, null);
        }
        if (repository.existsByEventoIdAndRut(prospecto.getEventoId(), prospecto.getRut())) {
            return duplicado(prospecto);
        }
        try {
            Prospecto guardado = repository.save(prospecto);
            return new ResultadoProspecto(guardado.getEventoId(), guardado.getRut(), EstadoResultado.CREADO, null, guardado.getId());
        } catch (DataIntegrityViolationException e) {
            return duplicado(prospecto);
        }
    }

    private ResultadoProspecto duplicado(Prospecto prospecto) {
        return new ResultadoProspecto(prospecto.getEventoId(), prospecto.getRut(), EstadoResultado.DUPLICADO, MSG_DUPLICADO, null);
    }

    private String revisarEvento(String eventoId, Map<String, String> eventosRevisados) {
        if (eventosRevisados.containsKey(eventoId)) {
            return eventosRevisados.get(eventoId);
        }
        Optional<EventoInfo> evento = eventoCliente.buscar(eventoId);
        String problema = null;
        if (evento.isEmpty()) {
            problema = "El evento " + eventoId + " no existe";
        } else if ("CERRADO".equalsIgnoreCase(evento.get().estado())) {
            problema = "El evento " + eventoId + " esta cerrado";
        }
        eventosRevisados.put(eventoId, problema);
        return problema;
    }

    private String revisar(ProspectoRequest request) {
        Set<ConstraintViolation<ProspectoRequest>> errores = validator.validate(request);
        if (errores.isEmpty()) {
            return null;
        }
        return errores.stream()
                .map(e -> e.getPropertyPath() + ": " + e.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));
    }

    private int contar(List<ResultadoProspecto> resultados, EstadoResultado estado) {
        return (int) resultados.stream().filter(r -> r.estado() == estado).count();
    }
}
