#!/bin/bash
set -euo pipefail

# ==============================
# CONFIGURATION
# ==============================

APP_NAME="bitelo-api"
IMAGE="ghcr.io/amir-hshahi/bitelo/bitelo-api:latest"
PORT=8080

# ==============================
# DEPLOYMENT
# ==============================

echo ">>> [$(date)] Logging in to GHCR..."

if [ -z "${GHCR_TOKEN:-}" ]; then
  echo "ERROR: GHCR_TOKEN is not set. Exiting."
  exit 1
fi

echo "${GHCR_TOKEN}" | docker login ghcr.io -u "Amir-HShahi" --password-stdin

# Pull image from GHCR
echo ">>> [$(date)] Pulling latest image from GHCR..."
docker pull "${IMAGE}"

# Stop running container (if any)
if docker ps -q -f name="${APP_NAME}" 2>/dev/null | grep -q .; then
  echo ">>> [$(date)] Stopping old container..."
  docker stop "${APP_NAME}"
fi

# Remove old container (if any)
if docker ps -aq -f name="${APP_NAME}" 2>/dev/null | grep -q .; then
  echo ">>> [$(date)] Removing old container..."
  docker rm "${APP_NAME}"
fi

# Run new container
echo ">>> [$(date)] Starting new container..."
docker run -d \
  --name "${APP_NAME}" \
  -p ${PORT}:8080 \
  --restart unless-stopped \
  "${IMAGE}"

echo ">>> [$(date)] Deployment complete!"
echo ">>> [$(date)] Container status:"
docker ps -f name="${APP_NAME}"