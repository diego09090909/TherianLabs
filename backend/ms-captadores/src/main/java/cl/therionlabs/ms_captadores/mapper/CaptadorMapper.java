package cl.therionlabs.ms_captadores.mapper;

import cl.therionlabs.ms_captadores.dto.CaptadorResponse;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import cl.therionlabs.ms_captadores.model.Captador;
import org.springframework.stereotype.Component;

@Component
public class CaptadorMapper {

    public Captador toEntity(RegistroCaptadorRequest request) {
        Captador captador = new Captador();
        captador.setNombre(request.getNombre().trim());
        captador.setRut(request.getRut().replace(".", "").trim().toUpperCase());
        captador.setCorreo(request.getCorreo().trim().toLowerCase());
        captador.setTelefono(request.getTelefono().trim());
        captador.setContrasena(request.getContrasena());
        return captador;
    }

    public CaptadorResponse toResponse(Captador captador) {
        return new CaptadorResponse(
                captador.getId(),
                captador.getNombre(),
                captador.getRut(),
                captador.getCorreo(),
                captador.getTelefono()
        );
    }
}