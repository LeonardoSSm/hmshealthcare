# Production Runbook

## 1. Pre-deploy checks

1. Ensure Docker Desktop is running.
2. Create `.env` from `.env.example`.
3. Fill secure values for:
   - `APP_JWT_SECRET` (long Base64 secret)
   - `APP_CORS_ALLOWED_ORIGINS` (real frontend domain)
   - `APP_BOOTSTRAP_ADMIN_EMAIL`
   - `APP_BOOTSTRAP_ADMIN_PASSWORD` (12+ chars)
4. Confirm seeded local users are disabled:
   - `APP_DISABLE_SEEDED_USERS=true`
5. Run preflight:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\prod-preflight.ps1 -EnvFile .env
```

## 2. Deploy

```powershell
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

## 3. Post-deploy smoke test

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\prod-smoke.ps1 -EnvFile .env
```

## 4. Operational checks

1. Backend health:
   - `GET /actuator/health` must return `UP`.
2. Frontend health:
   - `GET /healthz` must return `200`.
3. Authentication:
   - Login with bootstrap admin must work.
4. Authorization:
   - Non-admin user must receive `403` on `/api/users`.
5. Persistence:
   - Create patient, admit, add medical record event, reload page and confirm data remains.

## 5. Hardening after go-live

1. Rotate bootstrap admin password and disable bootstrap if desired:
   - `APP_BOOTSTRAP_ADMIN_ENABLED=false` (after at least one trusted admin exists).
2. Keep backups and DB restore procedure tested.
3. Review logs for `401/403/500` spikes.
4. Keep dependencies and container base images updated.
