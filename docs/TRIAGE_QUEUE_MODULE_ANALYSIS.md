# Etapa 1 - Analise do Modulo de Fila, Classificacao de Risco e Chamada em TV

## Objetivo do cenario

Fluxo desejado:

1. Recepcao cadastra paciente e realiza entrada no atendimento.
2. Paciente entra na fila de espera para classificacao de risco.
3. Enfermagem classifica risco.
4. Paciente aguarda chamada para consultorio medico.
5. Medico atende e finaliza orientacoes/conduta.
6. Tela de TV exibe chamadas e estado da fila em tempo quase real.

## Estado atual do sistema

### Backend existente

- Modulos prontos:
  - Pacientes (`/api/patients`)
  - Internacoes (`/api/admissions`)
  - Prontuario (`/api/medical-records`)
  - Leitos (`/api/beds`)
  - Usuarios (`/api/users`)
  - Auth JWT (`/api/auth/*`)
- RBAC com papeis: `ADMIN`, `DOCTOR`, `NURSE`, `RECEPTIONIST`.
- Padrao de arquitetura: `domain` + `application` + `presentation` + `infrastructure`.
- Erros padronizados com `ApiErrorResponse` e `traceId`.

### Frontend existente

- Modulos prontos:
  - Dashboard
  - Pacientes
  - Prontuarios
  - Internacoes
  - Mapa de leitos
  - Usuarios
- Estrutura modular com `services`, `hooks`, `pages`, `components`.
- Auth JWT com refresh e rotas protegidas.

### Banco existente

- Tabelas atuais cobrem pacientes, internacoes, leitos, prontuario, usuarios, tokens.
- Nao existe tabela de fila de atendimento/triagem/consultorio.

## Lacunas para atender o novo modulo

1. Nao existe entidade de atendimento ambulatorial/emergencial (fila).
2. Nao existe maquina de estados para transicao:
   - aguardando triagem
   - em triagem
   - aguardando medico
   - em consulta
   - finalizado
3. Nao existe registro formal de classificacao de risco.
4. Nao existe endpoint de painel publico para TV.
5. Nao existe controle de concorrencia para evitar dupla chamada simultanea do mesmo paciente.
6. Nao existe historico de transicoes do fluxo de atendimento.

## Decisoes arquiteturais recomendadas

### 1) Novo contexto de dominio: Atendimento (`attendance`)

Criar modulo dedicado, sem acoplar em `admissions` (internacao) para nao misturar fluxo ambulatorial com ocupacao de leito.

### 2) Modelo de dados novo

Criar tabela principal `attendances` com:

- `id` (UUID)
- `ticket_number` (senha visivel)
- `patient_id` (FK pacientes)
- `status` (estado da maquina)
- `risk_level` (classificacao)
- `priority_score` (ordenacao da fila)
- `called_to` (TRIAGE/DOCTOR)
- `room_label` (consultorio/sala)
- `doctor_id` e `nurse_id` (FK users, opcional por etapa)
- timestamps de cada etapa (`checkin_at`, `triage_at`, `consultation_at`, `finished_at`)
- `outcome` (orientacao final)
- `notes`
- `created_at`, `updated_at`
- `version` (`@Version`) para controle otimista de concorrencia

Criar tabela `attendance_status_history` para auditoria de transicoes.

### 3) Estados e transicoes oficiais

Estados:

- `WAITING_TRIAGE`
- `IN_TRIAGE`
- `WAITING_DOCTOR`
- `CALLED_DOCTOR`
- `IN_CONSULTATION`
- `COMPLETED`
- `CANCELLED`

Transicoes permitidas:

- check-in -> `WAITING_TRIAGE`
- iniciar triagem -> `IN_TRIAGE`
- concluir triagem -> `WAITING_DOCTOR`
- chamar medico -> `CALLED_DOCTOR`
- iniciar consulta -> `IN_CONSULTATION`
- concluir consulta -> `COMPLETED`
- cancelar -> `CANCELLED`

### 4) API do modulo

Interna (autenticada):

- `POST /api/attendances/check-in`
- `GET /api/attendances` (filtros por status/data)
- `POST /api/attendances/{id}/start-triage`
- `POST /api/attendances/{id}/finish-triage`
- `POST /api/attendances/{id}/call-doctor`
- `POST /api/attendances/{id}/start-consultation`
- `POST /api/attendances/{id}/finish-consultation`
- `POST /api/attendances/{id}/cancel`

Painel TV (exibicao):

- `GET /api/panel/queue` (somente dados minimos de exibicao)

### 5) Seguranca e LGPD

- TV deve mostrar somente:
  - senha (`ticket_number`)
  - nome mascarado (ex: `MARIA S.`)
  - sala/consultorio
  - etapa atual
- Nao exibir CPF, email, telefone, diagnostico.
- Endpoint de painel deve retornar apenas DTO sanitizado.
- Controle de permissao:
  - recepcao/admin: check-in e chamada basica
  - enfermagem/admin: triagem
  - medico/admin: consulta e finalizacao

### 6) Tempo real

Para primeira versao robusta e simples:

- usar polling com React Query (`refetchInterval` de 5s) no painel TV e na tela operacional.
- WebSocket pode ser fase 2 de performance.

## Impacto no sistema atual e adaptacoes

1. `patients` permanece como cadastro mestre.
2. `medical_records` recebe eventos automaticos no fechamento de triagem/consulta (opcional na v1, recomendado na v1.1).
3. `admissions` continua para internacao com leito; fluxo novo apenas encaminha, nao substitui.
4. Dashboard pode ganhar indicadores novos:
   - aguardando triagem
   - aguardando medico
   - em consulta

## Plano de implementacao (proximas etapas)

## Etapa 2 - Backend do modulo

- Criar dominio `attendance` (entidade, enums, regras de transicao).
- Criar migrations `V15+` para tabelas e indices.
- Criar repositorio JPA + mapeadores.
- Criar casos de uso e controller com RBAC.
- Criar endpoint sanitizado para painel TV.
- Testes unitarios e de integracao do fluxo completo.

## Etapa 3 - Frontend operacional

- Criar pagina `Atendimentos` para equipe interna.
- Fluxos com modais:
  - check-in
  - triagem
  - chamada para medico
  - inicio/fim consulta
- Filtros por etapa, risco e tempo de espera.
- Tratamento de erros padronizado.

## Etapa 4 - Tela TV

- Criar rota publica `/panel`.
- Layout full-screen, alto contraste, legibilidade a distancia.
- Auto refresh (5s) e estados de loading/falha.
- Exibir somente dados sanitizados.

## Etapa 5 - Go-live e confiabilidade

- Seed opcional de salas/consultorios.
- Smoke tests de fluxo ponta a ponta.
- Checklist operacional (preflight + deploy + smoke).
- Monitorar logs de transicao e conflitos de concorrencia.

## Criterios de aceite do novo modulo

1. Paciente check-inado aparece imediatamente na fila.
2. Triagem altera prioridade e muda etapa corretamente.
3. Chamada para medico aparece na TV sem dados sensiveis.
4. Conflitos de dupla acao no mesmo atendimento retornam erro consistente.
5. Recarregar pagina nao perde estado (persistencia 100% banco).
6. RBAC bloqueia acoes fora do papel.
