package cl.therionlabs.eventos.mapper;

import cl.therionlabs.eventos.dto.EventoActualizarRequest;
import cl.therionlabs.eventos.dto.EventoRequest;
import cl.therionlabs.eventos.dto.EventoResponse;
import cl.therionlabs.eventos.model.EstadoEvento;
import cl.therionlabs.eventos.model.Evento;
import org.springframework.stereotype.Component;

@Component
public class EventoMapper {

    public Evento aEntidad(EventoRequest request) {
        Evento evento = new Evento();
        evento.setId(normalizarId(request.id()));
        evento.setNombre(request.nombre().trim());
        evento.setLiceo(request.liceo().trim());
        evento.setFecha(request.fecha());
        evento.setEstado(EstadoEvento.ABIERTO);
        return evento;
    }

    public void actualizar(Evento evento, EventoActualizarRequest request) {
        evento.setNombre(request.nombre().trim());
        evento.setLiceo(request.liceo().trim());
        evento.setFecha(request.fecha());
    }

    public EventoResponse aRespuesta(Evento e) {
        return new EventoResponse(e.getId(), e.getNombre(), e.getLiceo(), e.getFecha(), e.getEstado());
    }

    public static String normalizarId(String id) {
        return id == null ? null : id.trim().toUpperCase();
    }
}
