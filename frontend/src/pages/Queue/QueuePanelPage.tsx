import { useEffect, useMemo, useState } from "react";
import { useQueuePanel } from "../../hooks/useAttendancesQueue";

const statusLabel: Record<string, string> = {
  WAITING_TRIAGE: "Aguardando triagem",
  IN_TRIAGE: "Em triagem",
  WAITING_DOCTOR: "Aguardando medico",
  CALLED_DOCTOR: "Dirija-se ao consultorio",
  IN_CONSULTATION: "Em consulta"
};

const riskTone: Record<string, string> = {
  RED: "Vermelho",
  ORANGE: "Laranja",
  YELLOW: "Amarelo",
  GREEN: "Verde",
  BLUE: "Azul"
};

function formatClock(date: Date): string {
  return new Intl.DateTimeFormat("pt-BR", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit"
  }).format(date);
}

function formatDate(date: Date): string {
  return new Intl.DateTimeFormat("pt-BR", {
    weekday: "long",
    day: "2-digit",
    month: "long"
  }).format(date);
}

function waitingMinutes(checkInAt: string): number {
  const timestamp = new Date(checkInAt).getTime();
  if (!Number.isFinite(timestamp)) {
    return 0;
  }
  return Math.max(0, Math.floor((Date.now() - timestamp) / 60000));
}

export default function QueuePanelPage() {
  const [now, setNow] = useState(() => new Date());
  const panelQuery = useQueuePanel();

  useEffect(() => {
    const timer = window.setInterval(() => setNow(new Date()), 1000);
    return () => window.clearInterval(timer);
  }, []);

  const items = panelQuery.data ?? [];

  const calledItems = useMemo(
    () =>
      items
        .filter((item) => item.status === "CALLED_DOCTOR" || item.status === "IN_CONSULTATION")
        .sort((a, b) => {
          const left = new Date(a.calledAt ?? a.checkInAt).getTime();
          const right = new Date(b.calledAt ?? b.checkInAt).getTime();
          return right - left;
        }),
    [items]
  );

  const waitingDoctor = useMemo(
    () =>
      items
        .filter((item) => item.status === "WAITING_DOCTOR")
        .sort((a, b) => {
          const leftRisk = a.riskLevel ?? "BLUE";
          const rightRisk = b.riskLevel ?? "BLUE";
          if (leftRisk === rightRisk) {
            return new Date(a.checkInAt).getTime() - new Date(b.checkInAt).getTime();
          }
          const order = ["RED", "ORANGE", "YELLOW", "GREEN", "BLUE"];
          return order.indexOf(leftRisk) - order.indexOf(rightRisk);
        }),
    [items]
  );

  const waitingTriage = useMemo(
    () => items.filter((item) => item.status === "WAITING_TRIAGE" || item.status === "IN_TRIAGE"),
    [items]
  );

  return (
    <main className="queue-panel-root">
      <header className="queue-panel-header">
        <div>
          <h1>Painel de Chamadas</h1>
          <p>MediCore HMS - acompanhamento em tempo real</p>
        </div>
        <div className="queue-panel-time">
          <strong>{formatClock(now)}</strong>
          <span>{formatDate(now)}</span>
        </div>
      </header>

      <section className="queue-panel-grid">
        <article className="queue-panel-card">
          <h2>Chamados para atendimento</h2>
          {panelQuery.isLoading ? <p className="queue-panel-muted">Atualizando painel...</p> : null}
          {panelQuery.isError ? <p className="queue-panel-error">Falha ao carregar painel.</p> : null}
          {!panelQuery.isLoading && !panelQuery.isError && calledItems.length === 0 ? (
            <p className="queue-panel-muted">Nenhum paciente chamado no momento.</p>
          ) : null}

          <ul className="queue-panel-call-list">
            {calledItems.slice(0, 8).map((item) => (
              <li key={item.attendanceId} className="queue-panel-call-item">
                <div>
                  <p className="queue-panel-ticket">{item.ticketNumber}</p>
                  <strong>{item.displayName}</strong>
                </div>
                <div className="queue-panel-call-meta">
                  <span>{statusLabel[item.status] ?? item.status}</span>
                  <strong>{item.roomLabel ?? "Aguardando sala"}</strong>
                </div>
              </li>
            ))}
          </ul>
        </article>

        <article className="queue-panel-card">
          <h2>Aguardando consulta</h2>
          {waitingDoctor.length === 0 ? <p className="queue-panel-muted">Sem pacientes aguardando medico.</p> : null}

          <ul className="queue-panel-waiting-list">
            {waitingDoctor.slice(0, 10).map((item) => (
              <li key={item.attendanceId} className="queue-panel-waiting-item">
                <div>
                  <strong>{item.ticketNumber}</strong>
                  <span>{item.displayName}</span>
                </div>
                <div>
                  <span className={`queue-panel-risk ${item.riskLevel?.toLowerCase() ?? "blue"}`}>
                    {riskTone[item.riskLevel ?? "BLUE"]}
                  </span>
                  <small>{waitingMinutes(item.checkInAt)} min</small>
                </div>
              </li>
            ))}
          </ul>
        </article>
      </section>

      <section className="queue-panel-footer">
        <h3>Fila de triagem</h3>
        <div className="queue-panel-marquee">
          {waitingTriage.length === 0 ? (
            <span>Nenhum paciente aguardando triagem.</span>
          ) : (
            waitingTriage.map((item) => (
              <span key={item.attendanceId}>
                {item.ticketNumber} - {item.displayName} ({statusLabel[item.status] ?? item.status})
              </span>
            ))
          )}
        </div>
      </section>
    </main>
  );
}
