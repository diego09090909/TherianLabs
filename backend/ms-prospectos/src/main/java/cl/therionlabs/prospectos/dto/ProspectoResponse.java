package cl.therionlabs.prospectos.dto;

public record ProspectoResponse(
        Long id,
        String eventoId,
        String captadorId,
        String rut,
        String nombres,
        String apellidos,
        String sexo,
        String fechaNacimiento,
        String comunaResidencia,
        String celular,
        String email,
        String actividad,
        String actividadOtra,
        String establecimiento,
        String comunaEstablecimiento,
        String jornada,
        String sedes,
        String nivelInteres,
        String afirmacion,
        String carrera1,
        String carrera2,
        String carrera3,
        Boolean autorizaContacto,
        Long fechaCaptura
) {
}
