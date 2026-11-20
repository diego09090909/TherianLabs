package cl.therionlabs.prospectos.exception;

import java.time.Instant;
import java.util.List;

public record ErrorRespuesta(
        int status,
        String error,
        String mensaje,
        List<String> detalles,
        Instant fecha
) {
}
