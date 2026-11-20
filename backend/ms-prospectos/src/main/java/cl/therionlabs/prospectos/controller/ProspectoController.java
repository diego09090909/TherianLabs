package cl.therionlabs.prospectos.controller;

import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.ProspectoResponse;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.service.ProspectoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/prospectos")
public class ProspectoController {

    private final ProspectoService service;

    public ProspectoController(ProspectoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProspectoResponse> listar(@RequestParam(required = false) String eventoId) {
        return service.listar(eventoId);
    }

    @GetMapping("/{id}")
    public ProspectoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProspectoResponse crear(@Valid @RequestBody ProspectoRequest request) {
        return service.crear(request);
    }

    @PostMapping("/lote")
    public RespuestaLote lote(@RequestBody List<ProspectoRequest> prospectos) {
        return service.procesarLote(prospectos);
    }

    @PutMapping("/{id}")
    public ProspectoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProspectoRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
