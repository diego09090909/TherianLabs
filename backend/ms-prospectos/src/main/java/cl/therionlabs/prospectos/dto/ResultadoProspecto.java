package cl.therionlabs.prospectos.dto;

public record ResultadoProspecto(
        String eventoId,
        String rut,
        EstadoResultado estado,
        String motivo,
        Long id
) {
}
