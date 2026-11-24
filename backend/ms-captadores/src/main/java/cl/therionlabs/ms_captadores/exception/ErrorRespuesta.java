package cl.therionlabs.ms_captadores.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorRespuesta(
        int status,
        String error,
        String mensaje,
        List<String> detalles,
        LocalDateTime fecha
) {
}
