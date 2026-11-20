package cl.therionlabs.prospectos.dto;

import cl.therionlabs.prospectos.validation.RutValido;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProspectoRequest(
        @NotBlank(message = "es obligatorio") String eventoId,
        String captadorId,
        @NotBlank(message = "es obligatorio") @RutValido String rut,
        @NotBlank(message = "es obligatorio") String nombres,
        @NotBlank(message = "es obligatorio") String apellidos,
        String sexo,
        String fechaNacimiento,
        String comunaResidencia,
        @NotBlank(message = "es obligatorio") String celular,
        @NotBlank(message = "es obligatorio") @Email(message = "no es un correo valido") String email,
        String actividad,
        String actividadOtra,
        String establecimiento,
        String comunaEstablecimiento,
        String jornada,
        String sedes,
        String nivelInteres,
        String afirmacion,
        @NotBlank(message = "es obligatorio") String carrera1,
        String carrera2,
        String carrera3,
        Boolean autorizaContacto,
        @NotNull(message = "es obligatorio") Long fechaCaptura
) {
}
