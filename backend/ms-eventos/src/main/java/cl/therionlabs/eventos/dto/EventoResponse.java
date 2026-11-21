package cl.therionlabs.eventos.dto;

import cl.therionlabs.eventos.model.EstadoEvento;

import java.time.LocalDate;

public record EventoResponse(
        String id,
        String nombre,
        String liceo,
        LocalDate fecha,
        EstadoEvento estado
) {
}
