# Despliegue en AWS (EC2 + Docker Compose)

Un solo servidor EC2 corre MySQL y los 3 microservicios. Se reinician solos si la instancia se reinicia.

## 1. Crear la instancia (consola de AWS → EC2 → Launch instance)

| Campo | Valor |
|---|---|
| Nombre | `therianlabs-backend` |
| Imagen (AMI) | Amazon Linux 2023 |
| Tipo | `t3.small` (2 GB). `t3.micro` funciona con el swap del script, pero más lento |
| Key pair | Crear una nueva (`therianlabs.pem`) y guardarla |
| Almacenamiento | 20 GB gp3 |

**Security group** (reglas de entrada):

| Tipo | Puerto | Origen |
|---|---|---|
| SSH | 22 | My IP |
| Custom TCP | 8081-8083 | 0.0.0.0/0 |

MySQL (3306) **no** se abre: solo lo usan los microservicios dentro del servidor.

## 2. IP fija

EC2 → Elastic IPs → Allocate → Associate a la instancia. Así la IP no cambia al reiniciar y el APK sigue funcionando.

## 3. Instalar y levantar

En la consola de la instancia: botón **Connect → EC2 Instance Connect** (no necesita instalar nada), y luego:

```bash
curl -fsSL https://raw.githubusercontent.com/diego09090909/TherianLabs/master/backend/deploy/instalar-ec2.sh -o instalar.sh
bash instalar.sh
```

La primera vez demora 5–10 min (descarga dependencias y compila).

## 4. Probar desde tu PC

```
http://<IP-ELASTICA>:8081/api/eventos
```

## 5. Apuntar la app a AWS

En `app-movil/gradle.properties` agregar:

```
API_HOST=<IP-ELASTICA>
```

Generar el APK firmado (Build → Generate Signed App Bundle / APK). Para volver al emulador, borrar esa línea.

## Actualizar después de un cambio en GitHub

```bash
cd ~/TherianLabs && git pull && cd backend && sudo docker compose up -d --build
```

## Comandos útiles

```bash
sudo docker compose ps                 # estado
sudo docker compose logs -f ms-captadores   # logs de un servicio
sudo docker compose down               # apagar (los datos quedan guardados)
```

## Costos

`t3.small` ≈ US$15/mes encendida 24/7. Detener la instancia cuando no se use (la Elastic IP asociada a una instancia detenida cobra poco). Configurar una alerta en **Billing → Budgets**.
