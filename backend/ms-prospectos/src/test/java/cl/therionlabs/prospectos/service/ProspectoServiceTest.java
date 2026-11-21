package cl.therionlabs.prospectos.service;

import cl.therionlabs.prospectos.DatosPrueba;
import cl.therionlabs.prospectos.client.EventoCliente;
import cl.therionlabs.prospectos.client.EventoInfo;
import cl.therionlabs.prospectos.dto.EstadoResultado;
import cl.therionlabs.prospectos.dto.ProspectoRequest;
import cl.therionlabs.prospectos.dto.ProspectoResponse;
import cl.therionlabs.prospectos.dto.RespuestaLote;
import cl.therionlabs.prospectos.exception.ConflictoException;
import cl.therionlabs.prospectos.exception.RecursoNoEncontradoException;
import cl.therionlabs.prospectos.exception.ServicioNoDisponibleException;
import cl.therionlabs.prospectos.exception.SolicitudInvalidaException;
import cl.therionlabs.prospectos.mapper.ProspectoMapper;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProspectoServiceTest {

    @Mock
    private ProspectoRepository repository;

    @Mock
    private EventoCliente eventoCliente;

    private ProspectoService service;

    @BeforeEach
    void preparar() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        service = new ProspectoService(repository, new ProspectoMapper(), validator, eventoCliente);
    }

    private void eventoAbierto(String id) {
        when(eventoCliente.buscar(id)).thenReturn(Optional.of(new EventoInfo(id, "ABIERTO")));
    }

    @Test
    void crearGuardaYNormalizaElRut() {
        ProspectoRequest request = DatosPrueba.request("EVT-0042", "12.345.678-5");
        eventoAbierto("EVT-0042");
        when(repository.existsByEventoIdAndRut("EVT-0042", "12345678-5")).thenReturn(false);
        when(repository.save(any(Prospecto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProspectoResponse respuesta = service.crear(request);

        assertEquals("12345678-5", respuesta.rut());
        assertEquals("EVT-0042", respuesta.eventoId());
    }

    @Test
    void crearRechazaDuplicadoEnElMismoEvento() {
        ProspectoRequest request = DatosPrueba.request("EVT-0042", "12345678-5");
        eventoAbierto("EVT-0042");
        when(repository.existsByEventoIdAndRut("EVT-0042", "12345678-5")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.crear(request));
        verify(repository, never()).save(any());
    }

    @Test
    void crearRechazaEventoInexistente() {
        ProspectoRequest request = DatosPrueba.request("EVT-0042", "12345678-5");
        when(eventoCliente.buscar("EVT-0042")).thenReturn(Optional.empty());

        SolicitudInvalidaException e = assertThrows(SolicitudInvalidaException.class, () -> service.crear(request));

        assertTrue(e.getMessage().contains("no existe"));
        verify(repository, never()).save(any());
    }

    @Test
    void crearRechazaEventoCerrado() {
        ProspectoRequest request = DatosPrueba.request("EVT-0042", "12345678-5");
        when(eventoCliente.buscar("EVT-0042")).thenReturn(Optional.of(new EventoInfo("EVT-0042", "CERRADO")));

        SolicitudInvalidaException e = assertThrows(SolicitudInvalidaException.class, () -> service.crear(request));

        assertTrue(e.getMessage().contains("cerrado"));
        verify(repository, never()).save(any());
    }

    @Test
    void crearPropagaSiMsEventosNoResponde() {
        ProspectoRequest request = DatosPrueba.request("EVT-0042", "12345678-5");
        when(eventoCliente.buscar("EVT-0042")).thenThrow(new ServicioNoDisponibleException("caido"));

        assertThrows(ServicioNoDisponibleException.class, () -> service.crear(request));
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
    void obtenerDevuelveElProspecto() {
        when(repository.findById(5L)).thenReturn(Optional.of(DatosPrueba.prospecto("EVT-1", "12345678-5")));

        assertEquals("12345678-5", service.obtener(5L).rut());
    }

    @Test
    void obtenerLanzaNoEncontradoSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtener(99L));
    }

    @Test
    void actualizarCambiaSoloLosCamposEditables() {
        Prospecto actual = DatosPrueba.prospecto("EVT-1", "12345678-5");
        when(repository.findById(5L)).thenReturn(Optional.of(actual));
        when(repository.save(any(Prospecto.class))).thenAnswer(inv -> inv.getArgument(0));
        ProspectoRequest cambios = DatosPrueba.request("OTRO", "11111111-1");

        ProspectoResponse respuesta = service.actualizar(5L, cambios);

        assertEquals("12345678-5", respuesta.rut());
        assertEquals("EVT-1", respuesta.eventoId());
    }

    @Test
    void eliminarBorraSiExiste() {
        when(repository.findById(5L)).thenReturn(Optional.of(DatosPrueba.prospecto("EVT-1", "12345678-5")));

        service.eliminar(5L);

        verify(repository).deleteById(5L);
    }

    @Test
    void eliminarLanzaNoEncontradoSiNoExiste() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(5L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void loteInformaResultadoPorCadaProspecto() {
        ProspectoRequest nuevo = DatosPrueba.request("EVT-1", "12345678-5");
        ProspectoRequest repetido = DatosPrueba.request("EVT-1", "11111111-1");
        ProspectoRequest malo = DatosPrueba.request("EVT-1", "12345678-9");
        eventoAbierto("EVT-1");
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
        assertTrue(respuesta.resultados().get(2).motivo().contains("rut"));
        assertNull(respuesta.resultados().get(3).rut());
        verify(eventoCliente, times(1)).buscar("EVT-1");
    }

    @Test
    void loteMarcaInvalidoSiElEventoNoExisteOEstaCerrado() {
        ProspectoRequest sinEvento = DatosPrueba.request("EVT-X", "12345678-5");
        ProspectoRequest cerrado = DatosPrueba.request("EVT-C", "11111111-1");
        when(eventoCliente.buscar("EVT-X")).thenReturn(Optional.empty());
        when(eventoCliente.buscar("EVT-C")).thenReturn(Optional.of(new EventoInfo("EVT-C", "CERRADO")));

        RespuestaLote respuesta = service.procesarLote(List.of(sinEvento, cerrado));

        assertEquals(2, respuesta.invalidos());
        assertTrue(respuesta.resultados().get(0).motivo().contains("no existe"));
        assertTrue(respuesta.resultados().get(1).motivo().contains("cerrado"));
        verify(repository, never()).save(any());
    }

    @Test
    void lotePropagaSiMsEventosNoResponde() {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12345678-5");
        when(eventoCliente.buscar("EVT-1")).thenThrow(new ServicioNoDisponibleException("caido"));

        assertThrows(ServicioNoDisponibleException.class, () -> service.procesarLote(List.of(request)));
        verify(repository, never()).save(any());
    }

    @Test
    void loteMarcaInvalidoSiFaltanCamposObligatorios() {
        ProspectoRequest base = DatosPrueba.request("EVT-1", "12345678-5");
        ProspectoRequest sinNombre = new ProspectoRequest(
                base.eventoId(), base.captadorId(), base.rut(), "", base.apellidos(), base.sexo(),
                base.fechaNacimiento(), base.comunaResidencia(), base.celular(), base.email(), base.actividad(),
                base.actividadOtra(), base.establecimiento(), base.comunaEstablecimiento(), base.jornada(),
                base.sedes(), base.nivelInteres(), base.afirmacion(), base.carrera1(), base.carrera2(),
                base.carrera3(), base.autorizaContacto(), base.fechaCaptura());

        RespuestaLote respuesta = service.procesarLote(List.of(sinNombre));

        assertEquals(1, respuesta.invalidos());
        assertTrue(respuesta.resultados().get(0).motivo().contains("nombres"));
    }

    @Test
    void loteMarcaDuplicadoSiLaBaseRechazaElGuardado() {
        ProspectoRequest request = DatosPrueba.request("EVT-1", "12345678-5");
        eventoAbierto("EVT-1");
        when(repository.existsByEventoIdAndRut("EVT-1", "12345678-5")).thenReturn(false);
        when(repository.save(any(Prospecto.class))).thenThrow(new DataIntegrityViolationException("duplicado"));

        RespuestaLote respuesta = service.procesarLote(List.of(request));

        assertEquals(1, respuesta.duplicados());
    }

    @Test
    void loteVacioOMuyGrandeSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () -> service.procesarLote(null));
        assertThrows(SolicitudInvalidaException.class, () -> service.procesarLote(List.of()));

        List<ProspectoRequest> grande = new ArrayList<>();
        for (int i = 0; i <= ProspectoService.MAX_LOTE; i++) {
            grande.add(DatosPrueba.request("EVT-1", "12345678-5"));
        }
        assertThrows(SolicitudInvalidaException.class, () -> service.procesarLote(grande));
    }
}
