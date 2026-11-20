package cl.therionlabs.prospectos.mapper;

import cl.therionlabs.prospectos.DatosPrueba;
import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.ProspectoResponse;
import cl.therionlabs.prospectos.model.Prospecto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProspectoMapperTest {

    private final ProspectoMapper mapper = new ProspectoMapper();

    @Test
    void aEntidadCopiaCamposYNormalizaElRut() {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12.345.678-5");

        Prospecto prospecto = mapper.aEntidad(request);

        assertEquals("12345678-5", prospecto.getRut());
        assertEquals("EVT-1", prospecto.getEventoId());
        assertEquals("captador-1", prospecto.getCaptadorId());
        assertEquals("Camila Andrea", prospecto.getNombres());
        assertEquals(1763600000000L, prospecto.getFechaCaptura());
        assertTrue(prospecto.getAutorizaContacto());
    }

    @Test
    void autorizaContactoNuloQuedaEnFalso() {
        ProspectoRequest base = DatosPrueba.request("EVT-1", "12345678-5");
        ProspectoRequest sinAutorizar = new ProspectoRequest(
                base.eventoId(), base.captadorId(), base.rut(), base.nombres(), base.apellidos(), base.sexo(),
                base.fechaNacimiento(), base.comunaResidencia(), base.celular(), base.email(), base.actividad(),
                base.actividadOtra(), base.establecimiento(), base.comunaEstablecimiento(), base.jornada(),
                base.sedes(), base.nivelInteres(), base.afirmacion(), base.carrera1(), base.carrera2(),
                base.carrera3(), null, base.fechaCaptura());

        assertFalse(mapper.aEntidad(sinAutorizar).getAutorizaContacto());
    }

    @Test
    void actualizarNoTocaRutNiEvento() {
        Prospecto existente = DatosPrueba.prospecto("EVT-1", "12345678-5");
        ProspectoRequest cambios = DatosPrueba.request("OTRO", "11111111-1");

        mapper.actualizar(existente, cambios);

        assertEquals("12345678-5", existente.getRut());
        assertEquals("EVT-1", existente.getEventoId());
    }

    @Test
    void aRespuestaCopiaTodosLosCampos() {
        Prospecto prospecto = DatosPrueba.prospecto("EVT-1", "12345678-5");
        prospecto.setId(9L);

        ProspectoResponse respuesta = mapper.aRespuesta(prospecto);

        assertEquals(9L, respuesta.id());
        assertEquals("12345678-5", respuesta.rut());
        assertEquals("Analista Programador", respuesta.carrera1());
        assertEquals("Maipú|Plaza Oeste", respuesta.sedes());
        assertEquals(1763600000000L, respuesta.fechaCaptura());
    }
}
