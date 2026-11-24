package cl.therionlabs.eventos.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadJwtTest {

    private static final String CUERPO = """
            {"id":"EVT-0900","nombre":"Feria de Prueba","liceo":"Liceo de Prueba","fecha":"2026-12-01"}
            """;

    @Autowired
    private MockMvc mvc;

    @Value("${app.jwt.secret}")
    private String secret;

    private SecretKey llaveDelEquipo() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private String token(SecretKey llave, String rol, String tipo, long vidaMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject("persona@duocuc.cl")
                .claim("id", 1)
                .claim("rol", rol)
                .claim("tipo", tipo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + vidaMs))
                .signWith(llave)
                .compact();
    }

    private MockHttpServletRequestBuilder crearEvento(String token) {
        MockHttpServletRequestBuilder req = post("/api/eventos")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO);
        return token == null ? req : req.header("Authorization", "Bearer " + token);
    }

    @Test
    void consultarEventosNoPideToken() throws Exception {
        mvc.perform(get("/api/eventos")).andExpect(status().isOk());
    }

    @Test
    void crearSinTokenResponde401() throws Exception {
        mvc.perform(crearEvento(null)).andExpect(status().isUnauthorized());
    }

    @Test
    void crearComoAdminResponde201() throws Exception {
        mvc.perform(crearEvento(token(llaveDelEquipo(), "ADMIN", "access", 60_000)))
                .andExpect(status().isCreated());
    }

    @Test
    void crearComoCaptadorResponde403() throws Exception {
        mvc.perform(crearEvento(token(llaveDelEquipo(), "CAPTADOR", "access", 60_000)))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenVencidoResponde401() throws Exception {
        mvc.perform(crearEvento(token(llaveDelEquipo(), "ADMIN", "access", -60_000)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenNoSirveComoAccesoResponde401() throws Exception {
        mvc.perform(crearEvento(token(llaveDelEquipo(), "ADMIN", "refresh", 60_000)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFirmadoConOtraLlaveResponde401() throws Exception {
        SecretKey otra = Jwts.SIG.HS256.key().build();
        mvc.perform(crearEvento(token(otra, "ADMIN", "access", 60_000)))
                .andExpect(status().isUnauthorized());
    }
}
