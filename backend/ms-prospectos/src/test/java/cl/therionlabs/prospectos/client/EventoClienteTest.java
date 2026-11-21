package cl.therionlabs.prospectos.client;

import cl.therionlabs.prospectos.exception.ServicioNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EventoClienteTest {

    private MockRestServiceServer servidor;
    private EventoCliente cliente;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://eventos");
        servidor = MockRestServiceServer.bindTo(builder).build();
        cliente = new EventoCliente(builder.build());
    }

    @Test
    void devuelveElEventoSiExiste() {
        servidor.expect(requestTo("http://eventos/api/eventos/EVT-1"))
                .andRespond(withSuccess("{\"id\":\"EVT-1\",\"nombre\":\"Feria\",\"estado\":\"ABIERTO\"}", MediaType.APPLICATION_JSON));

        Optional<EventoInfo> evento = cliente.buscar("EVT-1");

        assertTrue(evento.isPresent());
        assertEquals("ABIERTO", evento.get().estado());
    }

    @Test
    void devuelveVacioSiNoExiste() {
        servidor.expect(requestTo("http://eventos/api/eventos/EVT-9"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertTrue(cliente.buscar("EVT-9").isEmpty());
    }

    @Test
    void lanzaServicioNoDisponibleSiMsEventosFalla() {
        servidor.expect(requestTo("http://eventos/api/eventos/EVT-1"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ServicioNoDisponibleException.class, () -> cliente.buscar("EVT-1"));
    }
}
