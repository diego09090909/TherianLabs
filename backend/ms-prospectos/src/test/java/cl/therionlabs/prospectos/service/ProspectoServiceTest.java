package cl.therionlabs.prospectos.service;

import cl.therionlabs.prospectos.DatosPrueba;
import cl.therionlabs.prospectos.dto.EstadoResultado;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.model.Prospecto;
import cl.therionlabs.prospectos.repository.ProspectoRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProspectoServiceTest {

    @Mock
    private ProspectoRepository repository;

    private ProspectoService service;

    @BeforeEach
    void preparar() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        service = new ProspectoService(repository, validator);
    }

    @Test
    void crearGuardaProspectoValido() {
        Prospecto p = DatosPrueba.prospecto("EVT-0042", "12.345.678-5");
        when(repository.existsByEventoIdAndRut("EVT-0042", "12345678-5")).thenReturn(false);
        when(repository.save(any(Prospecto.class))).thenAnswer(inv -> inv.getArgument(0));

        Prospecto guardado = service.crear(p);

        assertEquals("12345678-5", guardado.getRut());
        verify(repository).save(p);
    }

    @Test
    void crearRechazaRutInvalido() {
        Prospecto p = DatosPrueba.prospecto("EVT-0042", "12345678-9");

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.crear(p));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void crearRechazaCamposObligatoriosVacios() {
        Prospecto p = DatosPrueba.prospecto("EVT-0042", "12345678-5");
        p.setNombres("");

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.crear(p));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }

    @Test
    void crearRechazaDuplicadoEnElMismoEvento() {
        Prospecto p = DatosPrueba.prospecto("EVT-0042", "12345678-5");
        when(repository.existsByEventoIdAndRut("EVT-0042", "12345678-5")).thenReturn(true);

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.crear(p));

        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void listarSinEventoDevuelveTodos() {
        when(repository.findAll()).thenReturn(List.of(DatosPrueba.prospecto("EVT-1", "12345678-5")));

        assertEquals(1, service.listar(null).size());
        assertEquals(1, service.listar(" ").size());
        verify(repository, never()).findByEventoId(any());
    }

    @Test
    void listarConEventoFiltraPorEvento() {
        when(repository.findByEventoId("EVT-1")).thenReturn(List.of(DatosPrueba.prospecto("EVT-1", "12345678-5")));

        assertEquals(1, service.listar("EVT-1").size());
    }

    @Test
    void obtenerLanza404SiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.obtener(99L));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

    @Test
    void actualizarCambiaSoloLosCamposEditables() {
        Prospecto actual = DatosPrueba.prospecto("EVT-1", "12345678-5");
        actual.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(actual));
        when(repository.save(any(Prospecto.class))).thenAnswer(inv -> inv.getArgument(0));
        Prospecto nuevo = DatosPrueba.prospecto("OTRO", "99999999-9");
        nuevo.setNombres("Valentina");
        nuevo.setCarrera1("Diseño Gráfico");

        Prospecto resultado = service.actualizar(5L, nuevo);

        assertEquals("Valentina", resultado.getNombres());
        assertEquals("Diseño Gráfico", resultado.getCarrera1());
        assertEquals("12345678-5", resultado.getRut());
        assertEquals("EVT-1", resultado.getEventoId());
    }

    @Test
    void eliminarBorraSiExiste() {
        when(repository.findById(5L)).thenReturn(Optional.of(DatosPrueba.prospecto("EVT-1", "12345678-5")));

        service.eliminar(5L);

        verify(repository).deleteById(5L);
    }

    @Test
    void eliminarLanza404SiNoExiste() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.eliminar(5L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void loteInformaResultadoPorCadaProspecto() {
        Prospecto nuevo = DatosPrueba.prospecto("EVT-1", "12345678-5");
        Prospecto repetido = DatosPrueba.prospecto("EVT-1", "11111111-1");
        Prospecto malo = DatosPrueba.prospecto("EVT-1", "12345678-9");
        when(repository.existsByEventoIdAndRut("EVT-1", "12345678-5")).thenReturn(false);
        when(repository.existsByEventoIdAndRut("EVT-1", "11111111-1")).thenReturn(true);
        when(repository.save(any(Prospecto.class))).thenAnswer(inv -> {
            Prospecto p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        RespuestaLote respuesta = service.procesarLote(Arrays.asList(nuevo, repetido, malo, null));

        assertEquals(4, respuesta.recibidos());
        assertEquals(1, respuesta.creados());
        assertEquals(1, respuesta.duplicados());
        assertEquals(2, respuesta.invalidos());
        assertEquals(EstadoResultado.CREADO, respuesta.resultados().get(0).estado());
        assertEquals(10L, respuesta.resultados().get(0).id());
        assertEquals(EstadoResultado.DUPLICADO, respuesta.resultados().get(1).estado());
        assertEquals(EstadoResultado.INVALIDO, respuesta.resultados().get(2).estado());
        assertNull(respuesta.resultados().get(3).rut());
    }

    @Test
    void loteMarcaDuplicadoSiLaBaseRechazaElGuardado() {
        Prospecto p = DatosPrueba.prospecto("EVT-1", "12345678-5");
        when(repository.existsByEventoIdAndRut("EVT-1", "12345678-5")).thenReturn(false);
        when(repository.save(any(Prospecto.class))).thenThrow(new DataIntegrityViolationException("duplicado"));

        RespuestaLote respuesta = service.procesarLote(List.of(p));

        assertEquals(1, respuesta.duplicados());
    }

    @Test
    void loteVacioOMuyGrandeSeRechaza() {
        assertThrows(ResponseStatusException.class, () -> service.procesarLote(null));
        assertThrows(ResponseStatusException.class, () -> service.procesarLote(List.of()));

        List<Prospecto> grande = new ArrayList<>();
        for (int i = 0; i <= ProspectoService.MAX_LOTE; i++) {
            grande.add(DatosPrueba.prospecto("EVT-1", "12345678-5"));
        }
        assertThrows(ResponseStatusException.class, () -> service.procesarLote(grande));
    }
}
