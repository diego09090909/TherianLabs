package cl.therionlabs.prospectos.controller;

import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.model.Prospecto;
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
    public List<Prospecto> listar(@RequestParam(required = false) String eventoId) {
        return service.listar(eventoId);
    }

    @GetMapping("/{id}")
    public Prospecto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prospecto crear(@Valid @RequestBody Prospecto prospecto) {
        return service.crear(prospecto);
    }

    @PostMapping("/lote")
    public RespuestaLote lote(@RequestBody List<Prospecto> prospectos) {
        return service.procesarLote(prospectos);
    }

    @PutMapping("/{id}")
    public Prospecto actualizar(@PathVariable Long id, @Valid @RequestBody Prospecto prospecto) {
        return service.actualizar(id, prospecto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
