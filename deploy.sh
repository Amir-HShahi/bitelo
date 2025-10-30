#!/bin/bash
set -euo pipefail  # safer: exit on error, undefined vars, or pipe failures

# ==============================
# CONFIGURATION
# ==============================

APP_NAME="bitelo-api"
IMAGE="ghcr.io/amir-hshahi/${APP_NAME}:latest"
PORT=8080

# ==============================
# DEPLOYMENT
# ==============================

echo ">>> [$(date)] Logging in to GHCR..."

# Use GHCR_TOKEN passed from GitHub Actions secrets
if [ -z "${GHCR_TOKEN:-}" ]; then
  echo "ERROR: GHCR_TOKEN is not set. Exiting."
  exit 1
fi

# Non-interactive GHCR login 
echo "${GHCR_TOKEN}" | docker login ghcr.io -u "Amir-HShahi" --password-stdin

echo ">>> [$(date)] Pulling latest image from GHCR..."
docker pull "${IMAGE}"

# Stop running container (if any)
if docker ps -q -f name="${APP_NAME}" >/dev/null; then
  echo ">>> [$(date)] Stopping old container..."
  docker stop "${APP_NAME}"
fi

# Remove old container (if any)
if docker ps -aq -f name="${APP_NAME}" >/dev/null; then
  echo ">>> [$(date)] Removing old container..."
  docker rm "${APP_NAME}"
fi

# remove old image to free disk
# docker image prune -f

echo ">>> [$(date)] Starting new container..."
docker run -d \
  --name "${APP_NAME}" \
  -p ${PORT}:8080 \
  --restart unless-stopped \
  "${IMAGE}"

echo ">>> [$(date)] Deployment complete!"
