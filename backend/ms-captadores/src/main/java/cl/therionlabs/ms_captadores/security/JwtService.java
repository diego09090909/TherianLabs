package cl.therionlabs.ms_captadores.security;

import cl.therionlabs.ms_captadores.model.Captador;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.admin-correos:}")
    private String adminCorreos;

    public String generarToken(Captador captador) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", captador.getId());
        claims.put("nombre", captador.getNombre());
        claims.put("rut", captador.getRut());
        claims.put("rol", esAdmin(captador.getCorreo()) ? "ADMIN" : "CAPTADOR");
        claims.put("tipo", "access");

        return Jwts.builder()
                .claims(claims)
                .subject(captador.getCorreo())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(obtenerLlaveFirma())
                .compact();
    }

    private boolean esAdmin(String correo) {
        if (adminCorreos == null || adminCorreos.isBlank() || correo == null) {
            return false;
        }
        return Arrays.stream(adminCorreos.split(","))
                .map(String::trim)
                .anyMatch(c -> c.equalsIgnoreCase(correo));
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean esTokenValido(String token, String correo) {
        final String correoToken = extraerCorreo(token);
        return (correoToken.equals(correo) && !estaTokenExpirado(token));
    }

    private boolean estaTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerLlaveFirma())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenerLlaveFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}