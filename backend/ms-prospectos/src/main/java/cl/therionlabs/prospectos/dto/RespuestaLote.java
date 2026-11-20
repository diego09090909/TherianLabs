package cl.therionlabs.prospectos.dto;

import java.util.List;

public record RespuestaLote(
        int recibidos,
        int creados,
        int duplicados,
        int invalidos,
        List<ResultadoProspecto> resultados
) {
}
