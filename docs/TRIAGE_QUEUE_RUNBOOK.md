# Modulo de Fila, Triagem e Painel TV

## Objetivo
Controlar o fluxo ambulatorial em tempo real:
1. Check-in do paciente
2. Triagem e classificacao de risco
3. Chamada para consultorio
4. Inicio e finalizacao da consulta
5. Exibicao publica no painel de TV

## Rotas Frontend
- Operacao interna: `/queue`
- Painel publico (TV): `/queue/panel`

## Fluxo Operacional
1. Recepcao registra entrada (`WAITING_TRIAGE`).
2. Enfermagem inicia triagem (`IN_TRIAGE`).
3. Enfermagem finaliza triagem com risco (`WAITING_DOCTOR`).
4. Recepcao/Enfermagem chama para consultorio (`CALLED_DOCTOR`).
5. Medico inicia consulta (`IN_CONSULTATION`).
6. Medico finaliza atendimento (`COMPLETED`).

## Endpoints Principais
- `POST /api/attendances/check-in`
- `GET /api/attendances`
- `POST /api/attendances/{id}/start-triage`
- `POST /api/attendances/{id}/finish-triage`
- `POST /api/attendances/{id}/call-doctor`
- `POST /api/attendances/{id}/start-consultation`
- `POST /api/attendances/{id}/finish-consultation`
- `POST /api/attendances/{id}/cancel`
- `GET /api/panel/queue` (publico, sem autenticacao)

## Regras de Perfil
- Check-in/cancelamento: `ADMIN`, `RECEPTIONIST`
- Triagem: `ADMIN`, `NURSE`
- Chamada para medico: `ADMIN`, `NURSE`, `RECEPTIONIST`
- Consulta: `ADMIN`, `DOCTOR`

## Persistencia
- Dados operacionais do modulo ficam no banco (`attendances`, `attendance_status_history`).
- Frontend nao salva fila/triagem/consulta em localStorage/sessionStorage.
- No navegador, apenas tema visual e sessao em memoria (token nao persistente em disco).

## Equipe de Apoio
- Lista de medicos ativos: `GET /api/users/doctors`
- Lista de enfermeiros ativos: `GET /api/users/nurses`

## Checklist de Uso
- Backend no ar com migration `V15` aplicada.
- Frontend no ar com rota `/queue`.
- TV apontando para `/queue/panel`.
- Perfis com usuarios ativos para medico e enfermagem.
