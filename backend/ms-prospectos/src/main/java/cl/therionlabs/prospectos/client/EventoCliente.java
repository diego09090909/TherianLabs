package cl.therionlabs.prospectos.client;

import cl.therionlabs.prospectos.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class EventoCliente {

    private final RestClient restClient;

    @Autowired
    public EventoCliente(RestClient.Builder builder, @Value("${eventos.url:http://localhost:8081}") String url) {
        this(construir(builder, url));
    }

    EventoCliente(RestClient restClient) {
        this.restClient = restClient;
    }

    public Optional<EventoInfo> buscar(String id) {
        try {
            EventoInfo info = restClient.get()
                    .uri("/api/eventos/{id}", id)
                    .retrieve()
                    .body(EventoInfo.class);
            return Optional.ofNullable(info);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("No se pudo consultar ms-eventos, intenta de nuevo mas tarde");
        }
    }

    private static RestClient construir(RestClient.Builder builder, String url) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(3000);
        fabrica.setReadTimeout(3000);
        return builder.baseUrl(url).requestFactory(fabrica).build();
    }
}
