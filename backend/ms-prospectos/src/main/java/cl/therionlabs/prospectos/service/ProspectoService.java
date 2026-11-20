package cl.therionlabs.prospectos.service;

import cl.therionlabs.prospectos.dto.EstadoResultado;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.dto.ResultadoProspecto;
import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.repository.ProspectoRepository;
import cl.therionlabs.prospectos.util.RutValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProspectoService {

    static final int MAX_LOTE = 500;

    private final ProspectoRepository repository;
    private final Validator validator;

    public ProspectoService(ProspectoRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Prospecto> listar(String eventoId) {
        if (eventoId != null && !eventoId.isBlank()) {
            return repository.findByEventoId(eventoId);
        }
        return repository.findAll();
    }

    public Prospecto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prospecto no encontrado"));
    }

    public Prospecto crear(Prospecto prospecto) {
        String error = revisar(prospecto);
        if (error != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
        if (repository.existsByEventoIdAndRut(prospecto.getEventoId(), prospecto.getRut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese RUT ya esta registrado en el evento");
        }
        prospecto.setId(null);
        return repository.save(prospecto);
    }

    public RespuestaLote procesarLote(List<Prospecto> lote) {
        if (lote == null || lote.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El lote esta vacio");
        }
        if (lote.size() > MAX_LOTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El lote no puede superar " + MAX_LOTE + " prospectos");
        }
        List<ResultadoProspecto> resultados = new ArrayList<>();
        for (Prospecto prospecto : lote) {
            resultados.add(procesarUno(prospecto));
        }
        return new RespuestaLote(
                resultados.size(),
                contar(resultados, EstadoResultado.CREADO),
                contar(resultados, EstadoResultado.DUPLICADO),
                contar(resultados, EstadoResultado.INVALIDO),
                resultados
        );
    }

    public Prospecto actualizar(Long id, Prospecto datos) {
        Prospecto actual = obtener(id);
        actual.setNombres(datos.getNombres());
        actual.setApellidos(datos.getApellidos());
        actual.setSexo(datos.getSexo());
        actual.setFechaNacimiento(datos.getFechaNacimiento());
        actual.setComunaResidencia(datos.getComunaResidencia());
        actual.setCelular(datos.getCelular());
        actual.setEmail(datos.getEmail());
        actual.setActividad(datos.getActividad());
        actual.setActividadOtra(datos.getActividadOtra());
        actual.setEstablecimiento(datos.getEstablecimiento());
        actual.setComunaEstablecimiento(datos.getComunaEstablecimiento());
        actual.setJornada(datos.getJornada());
        actual.setSedes(datos.getSedes());
        actual.setNivelInteres(datos.getNivelInteres());
        actual.setAfirmacion(datos.getAfirmacion());
        actual.setCarrera1(datos.getCarrera1());
        actual.setCarrera2(datos.getCarrera2());
        actual.setCarrera3(datos.getCarrera3());
        actual.setAutorizaContacto(datos.getAutorizaContacto());
        return repository.save(actual);
    }

    public void eliminar(Long id) {
        obtener(id);
        repository.deleteById(id);
    }

    private ResultadoProspecto procesarUno(Prospecto prospecto) {
        if (prospecto == null) {
            return new ResultadoProspecto(null, null, EstadoResultado.INVALIDO, "Prospecto vacio", null);
        }
        String error = revisar(prospecto);
        if (error != null) {
            return new ResultadoProspecto(prospecto.getEventoId(), prospecto.getRut(), EstadoResultado.INVALIDO, error, null);
        }
        if (repository.existsByEventoIdAndRut(prospecto.getEventoId(), prospecto.getRut())) {
            return duplicado(prospecto);
        }
        prospecto.setId(null);
        try {
            Prospecto guardado = repository.save(prospecto);
            return new ResultadoProspecto(guardado.getEventoId(), guardado.getRut(), EstadoResultado.CREADO, null, guardado.getId());
        } catch (DataIntegrityViolationException e) {
            return duplicado(prospecto);
        }
    }

    private ResultadoProspecto duplicado(Prospecto prospecto) {
        return new ResultadoProspecto(prospecto.getEventoId(), prospecto.getRut(), EstadoResultado.DUPLICADO,
                "Ese RUT ya esta registrado en el evento", null);
    }

    private String revisar(Prospecto prospecto) {
        Set<ConstraintViolation<Prospecto>> errores = validator.validate(prospecto);
        if (!errores.isEmpty()) {
            return errores.stream()
                    .map(e -> e.getPropertyPath() + ": " + e.getMessage())
                    .sorted()
                    .collect(Collectors.joining("; "));
        }
        prospecto.setRut(RutValidator.normalizar(prospecto.getRut()));
        if (!RutValidator.esValido(prospecto.getRut())) {
            return "RUT invalido";
        }
        return null;
    }

    private int contar(List<ResultadoProspecto> resultados, EstadoResultado estado) {
        return (int) resultados.stream().filter(r -> r.estado() == estado).count();
    }
}
