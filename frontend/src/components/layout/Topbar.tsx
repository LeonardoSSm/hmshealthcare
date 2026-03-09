import { useMemo } from "react";
import { useLocation } from "react-router-dom";
import { Icon } from "../ui/Icon";

interface TopbarProps {
  darkMode: boolean;
  onToggleDarkMode: () => void;
}

const pageHeaderByPath: Record<string, { title: string; subtitle: string }> = {
  "/dashboard": { title: "Dashboard", subtitle: "Visao geral do hospital" },
  "/queue": { title: "Fila de Atendimento", subtitle: "Fluxo de triagem e consulta" },
  "/patients": { title: "Pacientes", subtitle: "Cadastro e gestao de pacientes" },
  "/records": { title: "Prontuarios", subtitle: "Historico clinico dos pacientes" },
  "/admissions": { title: "Internacoes", subtitle: "Admissoes e altas hospitalares" },
  "/beds": { title: "Mapa de Leitos", subtitle: "Status e disponibilidade de leitos" },
  "/users": { title: "Usuarios", subtitle: "Gestao de usuarios e acessos" }
};

export function Topbar({ darkMode, onToggleDarkMode }: TopbarProps) {
  const location = useLocation();

  const pageHeader = pageHeaderByPath[location.pathname] ?? {
    title: "MediCore",
    subtitle: "Hospital Management System"
  };

  const now = useMemo(
    () =>
      new Intl.DateTimeFormat("pt-BR", {
        weekday: "long",
        day: "2-digit",
        month: "long"
      }).format(new Date()),
    []
  );

  return (
    <header className="topbar">
      <div className="topbar-left">
        <h1>{pageHeader.title}</h1>
        <p>{pageHeader.subtitle}</p>
      </div>
      <div className="topbar-right">
        <span className="topbar-date">{now}</span>
        <button className="dark-toggle" onClick={onToggleDarkMode} type="button" title="Alternar tema">
          <Icon name={darkMode ? "sun" : "moon"} />
        </button>
        <button className="topbar-notification" type="button" title="Notificacoes">
          <Icon name="bell" />
          <span className="topbar-notification-dot" />
        </button>
      </div>
    </header>
  );
}
