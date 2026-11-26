#!/bin/bash
# Prepara una instancia EC2 (Amazon Linux 2023) y levanta los microservicios.
# Uso (conectado por SSH a la instancia):
#   curl -fsSL https://raw.githubusercontent.com/diego09090909/TherianLabs/master/backend/deploy/instalar-ec2.sh -o instalar.sh
#   bash instalar.sh
set -e

# 1) Docker, Git y Docker Compose
sudo dnf install -y docker git
sudo systemctl enable --now docker
sudo usermod -aG docker ec2-user
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -fsSL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-$(uname -m)" \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# 2) 2 GB de swap: 3 microservicios + MySQL no caben en 1 GB de RAM
if [ ! -f /swapfile ]; then
  sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
  sudo chmod 600 /swapfile
  sudo mkswap /swapfile
  sudo swapon /swapfile
  echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
fi

# 3) Código
cd ~
if [ ! -d TherianLabs ]; then
  git clone https://github.com/diego09090909/TherianLabs.git
fi
cd TherianLabs && git pull
cd backend

# 4) Variables (claves) solo la primera vez
if [ ! -f .env ]; then
  echo "DB_PASSWORD=$(openssl rand -hex 16)" > .env
  echo "JWT_SECRET=$(openssl rand -base64 48 | tr -d '\n')" >> .env
  echo "ADMIN_CORREOS=" >> .env
  echo "Se creó backend/.env con claves nuevas."
fi

# 5) Construir y levantar
sudo docker compose up -d --build
sudo docker compose ps
echo "Listo. Prueba: curl http://localhost:8081/api/eventos"
