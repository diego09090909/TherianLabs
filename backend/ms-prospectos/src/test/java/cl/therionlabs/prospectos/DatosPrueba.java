package cl.therionlabs.prospectos;

import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.ProspectoResponse;
import cl.therionlabs.prospectos.mapper.ProspectoMapper;
import cl.therionlabs.prospectos.model.Prospecto;

public final class DatosPrueba {

    private static final ProspectoMapper MAPPER = new ProspectoMapper();

    private DatosPrueba() {
    }

    public static ProspectoRequest request(String eventoId, String rut) {
        return new ProspectoRequest(
                eventoId,
                "captador-1",
                rut,
                "Camila Andrea",
                "Rojas Soto",
                "Femenino",
                "30-05-2008",
                "Maipú",
                "912345678",
                "camila@correo.cl",
                "4° medio",
                null,
                "Liceo A-90",
                "Maipú",
                "Diurna",
                "Maipú|Plaza Oeste",
                "Alto",
                "2",
                "Analista Programador",
                "Ingeniería en Informática",
                "Ingeniería en Ciberseguridad",
                true,
                1763600000000L
        );
    }

    public static Prospecto prospecto(String eventoId, String rut) {
        return MAPPER.aEntidad(request(eventoId, rut));
    }

    public static ProspectoResponse respuesta(String eventoId, String rut) {
        return MAPPER.aRespuesta(prospecto(eventoId, rut));
    }
}
