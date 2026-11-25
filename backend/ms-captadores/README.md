# ms-captadores (puerto 8083)

Microservicio de captadores: registro, login con JWT y gestión de cuentas. **Responsable: Diego.**

Spring Boot 4.1.2 · Java 17 · Spring Security + JWT (jjwt 0.12.6) · JPA · MySQL (`captadores_db`).

## Ejecutar

```
.\mvnw spring-boot:run
```

Variables opcionales: `DB_USER` (root), `DB_PASSWORD` (vacía), `JWT_SECRET` (clave Base64 de 32 bytes o más), `JWT_EXPIRATION` (ms, 30 días por defecto) y `ADMIN_CORREOS` (correos separados por coma que reciben el rol `ADMIN`). La base `captadores_db` se crea sola.

## Rutas

| Método | Ruta | Auth | Respuesta |
|---|---|---|---|
| POST | `/api/captadores/solicitudes` | pública | 201 + token y captador |
| POST | `/api/captadores/login` | pública | 200 + token y captador |
| GET | `/api/captadores` | Bearer | 200 lista |
| GET | `/api/captadores/me` | Bearer | 200 captador autenticado |
| GET | `/api/captadores/{id}` | Bearer | 200 / 404 |
| PUT | `/api/captadores/{id}` | Bearer (solo la propia cuenta) | 200 / 403 / 404 |
| DELETE | `/api/captadores/{id}` | Bearer (solo la propia cuenta) | 204 / 403 / 404 |

Registro: `nombre`, `rut` (módulo 11), `correo` institucional (`duocuc.cl`, `duoc.cl`, `profesor.duoc.cl`), `telefono` (8-20), `contrasena` (mín. 6). La contraseña se guarda con BCrypt y nunca se devuelve.

## Token JWT

El token incluye los claims `id`, `nombre`, `rut`, `rol` y `tipo = "access"`. El `rol` es `CAPTADOR`, o `ADMIN` si el correo está en `ADMIN_CORREOS`. ms-eventos exige el rol `ADMIN` para crear, editar, abrir, cerrar y eliminar eventos, y valida el token con la misma clave: `JWT_SECRET` aquí debe ser igual a `app.jwt.secret` de ms-eventos.

## Errores

Formato único: `{status, error, mensaje, detalles, fecha}`. 400 datos inválidos o JSON roto · 401 sin token o credenciales incorrectas · 403 cuenta ajena · 404 no existe · 409 correo o RUT repetido.

## Tests

```
.\mvnw test
```

Reporte de cobertura: `target/site/jacoco/index.html`. Los tests usan H2 en memoria (no necesitan MySQL).
