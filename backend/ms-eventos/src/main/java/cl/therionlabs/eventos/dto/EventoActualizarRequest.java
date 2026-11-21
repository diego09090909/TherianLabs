package cl.therionlabs.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EventoActualizarRequest(
        @NotBlank(message = "es obligatorio") String nombre,
        @NotBlank(message = "es obligatorio") String liceo,
        @NotNull(message = "es obligatorio") LocalDate fecha
) {
}
