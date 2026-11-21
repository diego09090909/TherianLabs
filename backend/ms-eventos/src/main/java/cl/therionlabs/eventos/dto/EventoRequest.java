package cl.therionlabs.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventoRequest(
        @NotBlank(message = "es obligatorio")
        @Size(max = 20, message = "no puede superar 20 caracteres")
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "solo puede tener letras, numeros y guiones")
        String id,
        @NotBlank(message = "es obligatorio") String nombre,
        @NotBlank(message = "es obligatorio") String liceo,
        @NotNull(message = "es obligatorio") LocalDate fecha
) {
}
