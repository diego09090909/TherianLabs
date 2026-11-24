# ms-eventos (puerto 8081)

Microservicio de eventos (ferias y charlas en liceos). **Responsable: José.**

Spring Boot 3.4.5 · Java 17 · JPA · MySQL (`eventos_db`). La app y ms-prospectos consultan aquí si un evento existe y está abierto.

## Ejecutar

```
.\mvnw spring-boot:run
```

Variables opcionales: `DB_URL`, `DB_USER` (root) y `DB_PASSWORD` (vacía). La base `eventos_db` se crea sola.

## Rutas

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/eventos` (opcional `?estado=ABIERTO` o `CERRADO`) | 200 lista |
| GET | `/api/eventos/{id}` | 200 / 404 |
| POST | `/api/eventos` | 201 / 400 / 409 |
| PUT | `/api/eventos/{id}` | 200 / 400 / 404 |
| PATCH | `/api/eventos/{id}/cerrar` | 200 / 404 |
| PATCH | `/api/eventos/{id}/abrir` | 200 / 404 |
| DELETE | `/api/eventos/{id}` | 204 / 404 |

Evento: `id` (código como `EVT-0042`, hasta 20 caracteres, letras, números y guiones; se guarda en mayúsculas), `nombre`, `liceo`, `fecha` (`aaaa-MM-dd`) y `estado` (`ABIERTO` al crear, luego `CERRADO` o `ABIERTO`). El `id` no se puede cambiar con PUT.

Ejemplo de POST:

```json
{ "id": "EVT-0042", "nombre": "Feria Vocacional 2026", "liceo": "Liceo Bicentenario", "fecha": "2026-10-14" }
```

## Seguridad (JWT)

Los `GET` son públicos (los usan la app y ms-prospectos). Crear, editar, cerrar, abrir y eliminar exigen el header `Authorization: Bearer <token>` de un usuario con rol `ADMIN`. El token lo emite ms-captadores, con los claims `id`, `rol` y `tipo = "access"`.

La clave va en `secrets.properties` (no se sube a Git), con la misma clave que ms-captadores:

```
app.jwt.secret=<clave base64>
```

Sin token o con token inválido responde 401; con token válido pero sin rol `ADMIN`, 403.

## Errores

Formato único: `{status, error, mensaje, detalles, fecha}`. 400 datos inválidos, JSON roto o parámetro inválido · 404 evento no existe · 409 código repetido.

## Tests

```
.\mvnw test
```

Reporte de cobertura: `target/site/jacoco/index.html`. Los tests usan H2 en memoria (no necesitan MySQL).
