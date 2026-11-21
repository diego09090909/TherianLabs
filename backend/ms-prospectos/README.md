# ms-prospectos (puerto 8082)

Microservicio de prospectos. **Responsable: Kevin.** Requiere Java 17, Maven y MySQL (usuario root/root por defecto; se cambia con `DB_USER` y `DB_PASSWORD`).

## Ejecutar

```bash
mvn spring-boot:run
mvn test
```

El reporte de cobertura (JaCoCo) queda en `target/site/jacoco/index.html`.

## Rutas

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/prospectos?eventoId=` | Lista (opcionalmente por evento) |
| GET | `/api/prospectos/{id}` | Obtiene uno |
| POST | `/api/prospectos` | Crea uno (201; 400 si es inválido; 409 si el RUT ya existe en el evento) |
| POST | `/api/prospectos/lote` | Sincronización: recibe una lista (máx. 500) y responde el resultado de cada uno |
| PUT | `/api/prospectos/{id}` | Actualiza (no cambia RUT ni evento) |
| DELETE | `/api/prospectos/{id}` | Elimina (204) |

## Reglas

- Antes de guardar, se consulta `GET /api/eventos/{id}` en `ms-eventos` (`eventos.url`, por defecto `http://localhost:8081`). Si el evento no existe o está `CERRADO`, el prospecto es inválido; si `ms-eventos` no responde, se devuelve 503 para que la app reintente.
- El RUT se valida con módulo 11 y se guarda como `12345678-5`.
- Un mismo RUT no puede repetirse dentro de un evento.
- `eventoId` y `captadorId` son texto (ej. `EVT-0042`).
- `sedes` se guarda separadas por `|`.

## Estructura

`controller` → `service` → `repository` → `model`, más:

- `dto`: `ProspectoRequest` (entrada, con validaciones), `ProspectoResponse` (salida) y los resultados de `/lote`.
- `mapper`: convierte entre DTO y entidad (normaliza el RUT).
- `validation`: anotación `@RutValido`.
- `exception`: excepciones propias y `ManejadorErrores`, que responde todos los errores con el mismo JSON.

## Formato de error

```json
{
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Hay campos invalidos",
  "detalles": ["rut: no es un RUT valido"],
  "fecha": "2026-11-20T20:00:00Z"
}
```

## Respuesta de `/lote`

```json
{
  "recibidos": 3, "creados": 1, "duplicados": 1, "invalidos": 1,
  "resultados": [
    {"eventoId": "EVT-1", "rut": "12345678-5", "estado": "CREADO", "motivo": null, "id": 7},
    {"eventoId": "EVT-1", "rut": "11111111-1", "estado": "DUPLICADO", "motivo": "...", "id": null},
    {"eventoId": "EVT-1", "rut": "12345678-9", "estado": "INVALIDO", "motivo": "RUT invalido", "id": null}
  ]
}
```
