package cl.therionlabs.ms_captadores.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cl.therionlabs.ms_captadores.dto.ActualizarCaptadorRequest;
import cl.therionlabs.ms_captadores.dto.CaptadorResponse;
import cl.therionlabs.ms_captadores.dto.RegistroCaptadorRequest;
import cl.therionlabs.ms_captadores.model.Captador;
import org.junit.jupiter.api.Test;

class CaptadorMapperTest {

    private final CaptadorMapper mapper = new CaptadorMapper();

    @Test
    void toEntityNormalizaLosCampos() {
        var req = new RegistroCaptadorRequest("  Kevin Gonzalez ", "12.345.678-k", " KE@DuocUC.cl ", " 987654321 ", "clave123");

        Captador c = mapper.toEntity(req);

        assertEquals("Kevin Gonzalez", c.getNombre());
        assertEquals("12345678-K", c.getRut());
        assertEquals("ke@duocuc.cl", c.getCorreo());
        assertEquals("987654321", c.getTelefono());
        assertEquals("clave123", c.getContrasena());
    }

    @Test
    void toResponseNoExponeLaContrasena() {
        Captador c = new Captador(7L, "Ana", "12345678-5", "ana@duocuc.cl", "987654321", "hash");

        CaptadorResponse r = mapper.toResponse(c);

        assertEquals(7L, r.getId());
        assertEquals("Ana", r.getNombre());
        assertEquals("12345678-5", r.getRut());
        assertEquals("ana@duocuc.cl", r.getCorreo());
        assertEquals("987654321", r.getTelefono());
    }

    @Test
    void actualizarCambiaSoloNombreYTelefono() {
        Captador c = new Captador(7L, "Ana", "12345678-5", "ana@duocuc.cl", "111111111", "hash");

        mapper.actualizar(c, new ActualizarCaptadorRequest(" Ana María ", " 999999999 "));

        assertEquals("Ana María", c.getNombre());
        assertEquals("999999999", c.getTelefono());
        assertEquals("ana@duocuc.cl", c.getCorreo());
        assertEquals("12345678-5", c.getRut());
        assertEquals("hash", c.getContrasena());
    }
}
