package cl.therionlabs.ms_captadores.controller;

import cl.therionlabs.ms_captadores.dto.ActualizarCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.AuthResponse;
import cl.therionlabs.ms_captadores.dto.CambiarContrasenaRequest;
import cl.therionlabs.ms_captadores.dto.CaptadorResponse;
import cl.therionlabs.ms_captadores.dto.LoginCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import cl.therionlabs.ms_captadores.mapper.CaptadorMapper;
import cl.therionlabs.ms_captadores.model.Captador;
import cl.therionlabs.ms_captadores.service.CaptadorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/captadores")
public class CaptadorController {

    private final CaptadorService service;
    private final CaptadorMapper mapper;

    public CaptadorController(CaptadorService service, CaptadorMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroCaptadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginCaptadorRequest request) {
        return service.login(request);
    }

    @GetMapping
    public List<CaptadorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/me")
    public CaptadorResponse yo(@AuthenticationPrincipal Captador captador) {
        return mapper.toResponse(captador);
    }

    @GetMapping("/{id}")
    public CaptadorResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    public CaptadorResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody ActualizarCaptadorRequest request,
                                       @AuthenticationPrincipal Captador captador) {
        return service.actualizar(id, request, captador);
    }

    @PutMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(@PathVariable Long id,
                                                  @Valid @RequestBody CambiarContrasenaRequest request,
                                                  @AuthenticationPrincipal Captador captador) {
        service.cambiarContrasena(id, request, captador);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal Captador captador) {
        service.eliminar(id, captador);
        return ResponseEntity.noContent().build();
    }
}
