# MediCore HMS

Monorepo com backend `Spring Boot 3 / Java 21` e frontend `React + Vite + TypeScript`.

## Stack

- Backend: Spring Web, Spring Security (JWT), JPA, Flyway, Actuator
- Frontend: React, React Router, TanStack Query, Axios
- Banco: MySQL 8
- Deploy: Docker + Docker Compose

## Estrutura

- `backend/` API REST
- `frontend/` aplicacao web
- `docker-compose.prod.yml` stack de producao

## Requisitos locais (dev)

- Java 21+
- Maven 3.9+
- Node 20+
- MySQL 8+

## Rodar em desenvolvimento

### Backend

```bash
cd backend
mvn spring-boot:run
```

Perfil padrao: `dev`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend em `http://localhost:5173` e backend em `http://localhost:8080/api`.

## Rodar em producao (Docker)

1. Copie `.env.example` para `.env` e preencha valores seguros.
2. Rode preflight de producao:

```bash
powershell -ExecutionPolicy Bypass -File .\scripts\prod-preflight.ps1 -EnvFile .env
```

3. Suba a stack:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

4. Acesse:
- Frontend: `http://<host>:80`
- Backend health: `http://<host>:8080/actuator/health`

5. Rode smoke test pos-deploy:

```bash
powershell -ExecutionPolicy Bypass -File .\scripts\prod-smoke.ps1 -EnvFile .env
```

## Seguranca e configuracao

- JWT e CORS por variaveis de ambiente:
  - `APP_JWT_SECRET`
  - `APP_CORS_ALLOWED_ORIGINS`
  - `APP_DISABLE_SEEDED_USERS` (`true` em producao para desativar usuarios seed `*.local`)
  - `APP_BOOTSTRAP_ADMIN_EMAIL` / `APP_BOOTSTRAP_ADMIN_PASSWORD` para garantir um admin inicial seguro
  - `APP_BOOTSTRAP_ADMIN_ENABLED` pode ser `false` depois da criacao de admins definitivos
- Configuracao por perfil:
  - `application-dev.yml`
  - `application-prod.yml`
- Sessao frontend mantida em memoria (nao persiste token em localStorage).

## Dados iniciais

- Em `prod`, recomenda-se:
  - `APP_DISABLE_SEEDED_USERS=true`
  - bootstrap de admin via `APP_BOOTSTRAP_ADMIN_*`
- Leitos padrao seed em `V12__seed_default_beds.sql`.

## Qualidade

- Frontend build:

```bash
cd frontend && npm run build
```

- Backend compile/test:

```bash
cd backend && mvn test
```

## Endpoints principais

- Auth: `/api/auth/*`
- Pacientes: `/api/patients`
- Prontuarios: `/api/medical-records`
- Internacoes: `/api/admissions`
- Leitos: `/api/beds`
- Usuarios: `/api/users`

## Operacao

- Runbook de producao: `docs/PRODUCTION_RUNBOOK.md`
