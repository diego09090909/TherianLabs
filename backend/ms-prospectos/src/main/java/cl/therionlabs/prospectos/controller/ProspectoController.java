package cl.therionlabs.prospectos.controller;

import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.service.ProspectoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prospectos")
public class ProspectoController {

    private final ProspectoService service;

    public ProspectoController(ProspectoService service) {
        this.service = service;
    }
    // >> QUE HACE: @RestController = recibe peticiones HTTP y responde JSON. Todas las rutas parten en /api/prospectos.

    @GetMapping
    public List<Prospecto> listar(@RequestParam(required = false) Long eventoId) {
        return service.listar(eventoId);
    }
    // >> QUE HACE: GET /api/prospectos  o  GET /api/prospectos?eventoId=1  -> lista.

    @GetMapping("/{id}")
    public Prospecto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
    // >> QUE HACE: GET /api/prospectos/5 -> devuelve el prospecto 5.

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prospecto crear(@Valid @RequestBody Prospecto prospecto) {
        return service.crear(prospecto);
    }
    // >> QUE HACE: POST /api/prospectos con JSON en el body -> crea (201). @Valid aplica los @NotBlank del modelo (400 si faltan).

    @PutMapping("/{id}")
    public Prospecto actualizar(@PathVariable Long id, @Valid @RequestBody Prospecto prospecto) {
        return service.actualizar(id, prospecto);
    }
    // >> QUE HACE: PUT /api/prospectos/5 -> modifica el prospecto 5.

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
    // >> QUE HACE: DELETE /api/prospectos/5 -> borra y responde 204 (sin cuerpo).
}
