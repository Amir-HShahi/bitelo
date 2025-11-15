# CI/CD Pipeline Documentation

## Overview

Automated pipeline using GitHub Actions for building, testing, and deploying application to cloud server.

**Stack:** Java 21, Maven, PostgreSQL 16, Docker, GitHub Container Registry, Ubuntu

## Pipeline Flow

```
PR → develop/master
  ↓
CI: Build & Test (develop or master)
  ↓
Dockerize: Build & Push to GHCR (master only)
  ↓ (on success)
Deploy: SSH to Production Server (master only)
```

## Workflows

### 1. CI Workflow (`ci.yml`)

**Triggers:** PR to `master` or `develop`

**Steps:**
- Sets up JDK 21 + PostgreSQL 16
- Runs `mvn clean verify`
- Validates all tests pass

### 2. Dockerize Workflow (`docker.yml`)

**Triggers:** PR to `master` only

**Steps:**
- Runs CI tests
- Builds Docker image
- Pushes to `ghcr.io/{owner}/{repo}/bitelo-api:latest`

**Requires:** `GITHUB_TOKEN` with `write:packages` permission

### 3. Deploy Workflow (`deploy.yml`)

**Triggers:** After successful Dockerize workflows

**Steps:**
- SSH into production server
- Runs `/opt/burgerman/deploy.sh`

**Requires:** `GITHUB_TOKEN` with `read:packages` permission

## Deployment Script (`deploy.sh`)

**Location:** `/opt/burgerman/deploy.sh` on production server

**What it does:**
1. Pulls latest image from GHCR
2. Creates Docker network if needed
3. Starts/creates PostgreSQL container (persistent data)
4. Stops old app container
5. Starts new app container
6. App runs on port 8080

**Configuration:**
- App: `bitelo-api` on port 8080
- DB: `bitelo-db` on port 5432
- Network: `bitelo-network`
- Volume: `bitelo-postgres-data`

---

## Required Secrets

Configure in `Settings → Secrets and variables → Actions`:

| Secret           | Description                                        |
| ---------------- | -------------------------------------------------- |
| `DB_USER`        | PostgreSQL username                                |
| `DB_PASSWORD`    | PostgreSQL password                                |
| `DB_DATABASE`    | Database name                                      |
| `GHCR_TOKEN`     | GitHub Personal Access Token (read/write packages) |
| `SERVER_HOST`    | Production server URL                              |
| `SERVER_USER`    | SSH username                                       |
| `SERVER_SSH_KEY` | Private SSH key                                    |

---

## Server Setup
- Create dedicated user for deploying
- Generate and save public and private keys for SSH login
- Setup docker 
- Save `deploy.sh` script in `/opt/burgerman`
- Configure firewall and nginx

**Prerequisites:**
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Create deployment directory
sudo mkdir -p /opt/burgerman
sudo chown $USER:$USER /opt/burgerman

# Upload and set permissions for deploy.sh
chmod +x /opt/burgerman/deploy.sh
```

## Development Workflow

1. Create feature branch → PR to `develop` → CI runs
2. Merge to `develop`
3. PR `develop` → `master` → CI + Dockerize run
4. Merge to `master` → Auto-deploy to production

## Troubleshooting

| Issue                         | Solution                                       |
| ----------------------------- | ---------------------------------------------- |
| PostgreSQL health check fails | Increase timeout/retries in workflow           |
| Docker push denied            | Check `GITHUB_TOKEN` has `write:packages`      |
| SSH connection refused        | Verify `SERVER_HOST` and SSH key setup         |
| Container won't start         | Check logs: `docker logs bitelo-api`           |
| Port in use                   | Stop conflicting process: `sudo lsof -i :8080` |

**View logs:**
```bash
docker logs bitelo-api
docker logs bitelo-db
```

**Restart deployment:**
```bash
ssh user@server
cd /opt/burgerman
./deploy.sh
```