import type { NavItem } from "../types/navigation.types";

export const NAV_ITEMS: NavItem[] = [
  { page: "dashboard", label: "Dashboard", icon: "chart" },
  { page: "queue", label: "Fila de Atendimento", icon: "queue" },
  { page: "patients", label: "Pacientes", icon: "user" },
  { page: "records", label: "Prontuarios", icon: "file" },
  { page: "admissions", label: "Internacoes", icon: "hospital" },
  { page: "beds", label: "Mapa de Leitos", icon: "bed" },
  { page: "users", label: "Usuarios", icon: "team", roles: ["ADMIN"] }
];
