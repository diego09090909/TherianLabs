# 🛠️ Backend · Microservicios

Microservicios Spring Boot de **Captura Digital Móvil de Prospectos**. Son independientes de la app Android (carpeta `app/`).

| Carpeta | Puerto | Base de datos | Responsable |
|---|---|---|---|
| `ms-eventos` | 8081 | `eventos_db` | José |
| `ms-prospectos` | 8082 | `prospectos_db` | Kevin |
| `ms-captadores` | 8083 | `captadores_db` | Diego |

## Reglas comunes

- Java 17 · Spring Boot 3.4 · Maven · MySQL.
- Capas: `controller` → `service` → `repository` → `model`.
- Rutas bajo `/api/<recurso>` (por ejemplo `/api/prospectos`).
- Cada servicio tiene su propio esquema de base de datos y no lee las tablas de los otros.
- Cada servicio se abre y se ejecuta por separado desde su carpeta (`mvn spring-boot:run`).
