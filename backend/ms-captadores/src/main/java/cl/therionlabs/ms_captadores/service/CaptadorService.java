package cl.therionlabs.ms_captadores.service;

import cl.therionlabs.ms_captadores.dto.ActualizarCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.AuthResponse;
import cl.therionlabs.ms_captadores.dto.CaptadorResponse;
import cl.therionlabs.ms_captadores.dto.LoginCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import cl.therionlabs.ms_captadores.exception.AccesoDenegadoException;
import cl.therionlabs.ms_captadores.exception.ConflictoException;
import cl.therionlabs.ms_captadores.exception.CredencialesInvalidasException;
import cl.therionlabs.ms_captadores.exception.RecursoNoEncontradoException;
import cl.therionlabs.ms_captadores.mapper.CaptadorMapper;
import cl.therionlabs.ms_captadores.model.Captador;
import cl.therionlabs.ms_captadores.repository.CaptadorRepository;
import cl.therionlabs.ms_captadores.security.JwtService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CaptadorService {

    private static final String TIPO_TOKEN = "Bearer";

    private final CaptadorRepository repository;
    private final CaptadorMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public CaptadorService(CaptadorRepository repository, CaptadorMapper mapper,
                           PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(RegistroCaptadorRequest request) {
        Captador captador = mapper.toEntity(request);

        if (repository.existsByCorreo(captador.getCorreo())) {
            throw new ConflictoException("Ya existe un captador con el correo " + captador.getCorreo());
        }
        if (repository.existsByRut(captador.getRut())) {
            throw new ConflictoException("Ya existe un captador con el RUT " + captador.getRut());
        }

        captador.setContrasena(passwordEncoder.encode(request.getContrasena()));
        Captador guardado = repository.save(captador);
        return respuestaAuth(guardado);
    }

    public AuthResponse login(LoginCaptadorRequest request) {
        String correo = request.getCorreo().trim().toLowerCase();

        Captador captador = repository.findByCorreo(correo)
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getContrasena(), captador.getContrasena())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return respuestaAuth(captador);
    }

    public List<CaptadorResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public CaptadorResponse obtener(Long id) {
        return mapper.toResponse(buscar(id));
    }

    public CaptadorResponse actualizar(Long id, ActualizarCaptadorRequest request, Captador solicitante) {
        verificarPropietario(id, solicitante);
        Captador captador = buscar(id);
        mapper.actualizar(captador, request);
        return mapper.toResponse(repository.save(captador));
    }

    public void eliminar(Long id, Captador solicitante) {
        verificarPropietario(id, solicitante);
        repository.delete(buscar(id));
    }

    private Captador buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el captador con id " + id));
    }

    private void verificarPropietario(Long id, Captador solicitante) {
        if (solicitante == null || !id.equals(solicitante.getId())) {
            throw new AccesoDenegadoException("Solo puedes modificar tu propia cuenta");
        }
    }

    private AuthResponse respuestaAuth(Captador captador) {
        return new AuthResponse(jwtService.generarToken(captador), TIPO_TOKEN, mapper.toResponse(captador));
    }
}
