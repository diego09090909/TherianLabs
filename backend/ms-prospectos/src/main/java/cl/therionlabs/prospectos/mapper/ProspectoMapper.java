package cl.therionlabs.prospectos.mapper;

import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.ProspectoResponse;
import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.util.RutValidator;
import org.springframework.stereotype.Component;

@Component
public class ProspectoMapper {

    public Prospecto aEntidad(ProspectoRequest request) {
        Prospecto prospecto = new Prospecto();
        prospecto.setEventoId(request.eventoId());
        prospecto.setCaptadorId(request.captadorId());
        prospecto.setRut(RutValidator.normalizar(request.rut()));
        prospecto.setFechaCaptura(request.fechaCaptura());
        copiarEditables(request, prospecto);
        return prospecto;
    }

    public void actualizar(Prospecto prospecto, ProspectoRequest request) {
        copiarEditables(request, prospecto);
    }

    public ProspectoResponse aRespuesta(Prospecto p) {
        return new ProspectoResponse(
                p.getId(),
                p.getEventoId(),
                p.getCaptadorId(),
                p.getRut(),
                p.getNombres(),
                p.getApellidos(),
                p.getSexo(),
                p.getFechaNacimiento(),
                p.getComunaResidencia(),
                p.getCelular(),
                p.getEmail(),
                p.getActividad(),
                p.getActividadOtra(),
                p.getEstablecimiento(),
                p.getComunaEstablecimiento(),
                p.getJornada(),
                p.getSedes(),
                p.getNivelInteres(),
                p.getAfirmacion(),
                p.getCarrera1(),
                p.getCarrera2(),
                p.getCarrera3(),
                p.getAutorizaContacto(),
                p.getFechaCaptura()
        );
    }

    private void copiarEditables(ProspectoRequest r, Prospecto p) {
        p.setNombres(r.nombres());
        p.setApellidos(r.apellidos());
        p.setSexo(r.sexo());
        p.setFechaNacimiento(r.fechaNacimiento());
        p.setComunaResidencia(r.comunaResidencia());
        p.setCelular(r.celular());
        p.setEmail(r.email());
        p.setActividad(r.actividad());
        p.setActividadOtra(r.actividadOtra());
        p.setEstablecimiento(r.establecimiento());
        p.setComunaEstablecimiento(r.comunaEstablecimiento());
        p.setJornada(r.jornada());
        p.setSedes(r.sedes());
        p.setNivelInteres(r.nivelInteres());
        p.setAfirmacion(r.afirmacion());
        p.setCarrera1(r.carrera1());
        p.setCarrera2(r.carrera2());
        p.setCarrera3(r.carrera3());
        p.setAutorizaContacto(Boolean.TRUE.equals(r.autorizaContacto()));
    }
}
