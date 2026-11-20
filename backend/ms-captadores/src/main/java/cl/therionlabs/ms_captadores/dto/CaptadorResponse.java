package cl.therionlabs.ms_captadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptadorResponse {

    private Long id;
    private String nombre;
    private String rut;
    private String correo;
    private String telefono;
}