package cl.therionlabs.eventos.config;

import cl.therionlabs.eventos.model.Evento;
import cl.therionlabs.eventos.repository.EventoRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Al arrancar crea el evento de demo EVT-0042 si no existe,
// así la app siempre tiene un evento válido para captar prospectos.
@Configuration
public class EventoDemoConfig {

    @Bean
    CommandLineRunner crearEventoDemo(EventoRepository repository) {
        return args -> {
            if (!repository.existsById("EVT-0042")) {
                Evento evento = new Evento();
                evento.setId("EVT-0042");
                evento.setNombre("Feria vocacional");
                evento.setLiceo("Liceo Bicentenario Ejemplo");
                evento.setFecha(LocalDate.now());
                repository.save(evento);
            }
        };
    }
}