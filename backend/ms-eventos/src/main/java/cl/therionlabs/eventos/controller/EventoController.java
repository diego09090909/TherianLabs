package cl.therionlabs.eventos.controller;

import cl.therionlabs.eventos.dto.EventoActualizarRequest;
import cl.therionlabs.eventos.dto.EventoRequest;
import cl.therionlabs.eventos.dto.EventoResponse;
import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventoResponse> listar(@RequestParam(required = false) EstadoEvento estado) {
        return service.listar(estado);
    }

    @GetMapping("/{id}")
    public EventoResponse obtener(@PathVariable String id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponse crear(@Valid @RequestBody EventoRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public EventoResponse actualizar(@PathVariable String id, @Valid @RequestBody EventoActualizarRequest request) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/cerrar")
    public EventoResponse cerrar(@PathVariable String id) {
        return service.cambiarEstado(id, EstadoEvento.CERRADO);
    }

    @PatchMapping("/{id}/abrir")
    public EventoResponse abrir(@PathVariable String id) {
        return service.cambiarEstado(id, EstadoEvento.ABIERTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminar(id);
    }
}
