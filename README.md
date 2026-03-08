# MediCore HMS

Monorepo com backend Spring Boot 3 (Java 21) e frontend React + Vite + Tailwind.

## Estrutura

- `backend/`: API REST com Clean Architecture + DDD.
- `frontend/`: Aplicacao web para autenticacao e gestao de pacientes.

## Ordem de implementacao seguida

1. Setup do projeto
2. Modelagem do dominio (sem JPA)
3. Migrations Flyway V1-V3
4. Persistencia (JPA + mappers + adapters)
5. Use case RegisterPatient
6. Controllers + testes de integracao
7. Autenticacao JWT + RBAC
8. Frontend com login e lista de pacientes

## Requisitos locais

- Java 21+
- Maven 3.9+
- Node 20+
- MySQL 8+

## Backend

1. Ajuste `backend/src/main/resources/application.yml` com usuario/senha do MySQL.
2. Execute:
   - `cd backend`
   - `mvn spring-boot:run`

API base: `http://localhost:8080/api`

Usuario seed (migration V8):
- email: `admin@medicore.local`
- senha: defina o hash desejado em `V8__insert_default_admin_user.sql`

## Frontend

1. Execute:
   - `cd frontend`
   - `npm install`
   - `npm run dev`

App web: `http://localhost:5173`
