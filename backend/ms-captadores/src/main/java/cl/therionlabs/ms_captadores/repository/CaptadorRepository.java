package cl.therionlabs.ms_captadores.repository;

import cl.therionlabs.ms_captadores.model.Captador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptadorRepository extends JpaRepository<Captador, Long> {

    Optional<Captador> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByRut(String rut);
}