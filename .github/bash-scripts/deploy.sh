#!/bin/bash
set -euo pipefail

# CONFIGURATION
APP_NAME="bitelo-api"
DB_NAME="bitelo-db"
NETWORK_NAME="bitelo-network"
IMAGE="ghcr.io/amir-hshahi/bitelo/bitelo-api:latest"
POSTGRES_IMAGE="postgres:16-alpine"
PORT=8080
DB_PORT=5432

# Validate required environment variables
required_vars=("GHCR_TOKEN" "DB_USER" "DB_PASSWORD" "DB_DATABASE")
for var in "${required_vars[@]}"; do
  if [ -z "${!var:-}" ]; then
    echo "ERROR: $var is not set. Exiting."
    exit 1
  fi
done

# DEPLOYMENT
echo ">>> [$(date)] Logging in to GHCR..."
echo "${GHCR_TOKEN}" | docker login ghcr.io -u "Amir-HShahi" --password-stdin

echo ">>> [$(date)] Pulling latest image from GHCR..."
docker pull "${IMAGE}"

# Create Docker network if it doesn't exist
if ! docker network inspect "${NETWORK_NAME}" >/dev/null 2>&1; then
  echo ">>> [$(date)] Creating Docker network..."
  docker network create "${NETWORK_NAME}"
fi

# DATABASE SETUP
# Check if PostgreSQL container is running
if docker ps -q -f name="${DB_NAME}" 2>/dev/null | grep -q .; then
  echo ">>> [$(date)] PostgreSQL container is already running"
else
  # Check if PostgreSQL container exists but is stopped
  if docker ps -aq -f name="${DB_NAME}" 2>/dev/null | grep -q .; then
    echo ">>> [$(date)] Starting existing PostgreSQL container..."
    docker start "${DB_NAME}"
  else
    echo ">>> [$(date)] Creating new PostgreSQL container..."
    docker run -d \
      --name "${DB_NAME}" \
      --network "${NETWORK_NAME}" \
      -e POSTGRES_USER="${DB_USER}" \
      -e POSTGRES_PASSWORD="${DB_PASSWORD}" \
      -e POSTGRES_DB="${DB_DATABASE}" \
      -v bitelo-postgres-data:/var/lib/postgresql/data \
      -p ${DB_PORT}:5432 \
      --restart unless-stopped \
      "${POSTGRES_IMAGE}"

    echo ">>> [$(date)] Waiting for PostgreSQL to be ready..."
    sleep 10
  fi
fi

# APPLICATION DEPLOYMENT
# Stop running app container (if any)
if docker ps -q -f name="${APP_NAME}" 2>/dev/null | grep -q .; then
  echo ">>> [$(date)] Stopping old application container..."
  docker stop "${APP_NAME}"
fi

# Remove old app container (if any)
if docker ps -aq -f name="${APP_NAME}" 2>/dev/null | grep -q .; then
  echo ">>> [$(date)] Removing old application container..."
  docker rm "${APP_NAME}"
fi

echo ">>> [$(date)] Starting new application container..."
docker run -d \
  --name "${APP_NAME}" \
  --network "${NETWORK_NAME}" \
  -p ${PORT}:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_NAME}:5432/${DB_DATABASE}" \
  -e SPRING_DATASOURCE_USERNAME="${DB_USER}" \
  -e SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}" \
  --restart unless-stopped \
  "${IMAGE}"

echo ">>> [$(date)] Deployment complete!"
echo ""
echo ">>> [$(date)] Container status:"
docker ps -f name="${DB_NAME}"
docker ps -f name="${APP_NAME}"
echo ""
echo ">>> [$(date)] Application should be available at: http://localhost:${PORT}"