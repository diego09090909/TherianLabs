package cl.therionlabs.prospectos.service;

import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.repository.ProspectoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProspectoService {

    private final ProspectoRepository repository;

    public ProspectoService(ProspectoRepository repository) {
        this.repository = repository;
    }
    // >> QUE HACE: @Service = capa de logica. Spring le entrega el repository solo (inyeccion por constructor).

    public List<Prospecto> listar(Long eventoId) {
        if (eventoId != null) {
            return repository.findByEventoId(eventoId);
        }
        return repository.findAll();
    }
    // >> QUE HACE: READ (lista). Si viene eventoId filtra por evento; si no, devuelve todos.

    public Prospecto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prospecto no encontrado"));
    }
    // >> QUE HACE: READ (uno). Si el id no existe responde 404.

    public Prospecto crear(Prospecto prospecto) {
        if (repository.existsByEventoIdAndRut(prospecto.getEventoId(), prospecto.getRut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese RUT ya esta registrado en el evento");
        }
        prospecto.setId(null);
        return repository.save(prospecto);
    }
    // >> QUE HACE: CREATE. Si el RUT ya existe en ese evento responde 409. setId(null) asegura que sea un registro nuevo.

    public Prospecto actualizar(Long id, Prospecto datos) {
        Prospecto actual = obtener(id);
        actual.setNombres(datos.getNombres());
        actual.setApellidos(datos.getApellidos());
        actual.setSexo(datos.getSexo());
        actual.setFechaNacimiento(datos.getFechaNacimiento());
        actual.setComunaResidencia(datos.getComunaResidencia());
        actual.setCelular(datos.getCelular());
        actual.setEmail(datos.getEmail());
        actual.setAutorizaContacto(datos.getAutorizaContacto());
        return repository.save(actual);
    }
    // >> QUE HACE: UPDATE. Busca el registro y cambia solo los campos editables. RUT y evento no se tocan.

    public void eliminar(Long id) {
        obtener(id);
        repository.deleteById(id);
    }
    // >> QUE HACE: DELETE. Primero verifica que exista (si no, 404) y luego lo borra.
}
