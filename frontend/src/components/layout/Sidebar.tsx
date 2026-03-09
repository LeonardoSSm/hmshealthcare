import { useMemo } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { NAV_ITEMS } from "../../config/navigation";
import { useAdmissions } from "../../hooks/useAdmissions";
import { useAuth } from "../../hooks/useAuth";
import { getInitials } from "../../lib/format";
import { useAuthStore } from "../../store/authStore";
import { Icon } from "../ui/Icon";

const pagePath: Record<string, string> = {
  dashboard: "/dashboard",
  queue: "/queue",
  patients: "/patients",
  records: "/records",
  admissions: "/admissions",
  beds: "/beds",
  users: "/users"
};

const roleLabel: Record<string, string> = {
  ADMIN: "Administrador",
  DOCTOR: "Medico",
  NURSE: "Enfermagem",
  RECEPTIONIST: "Recepcao"
};

export function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const session = useAuthStore((state) => state.session);
  const { logout } = useAuth();
  const { data: admissions = [] } = useAdmissions();

  const activeAdmissionsCount = admissions.filter((admission) => admission.status === "ACTIVE").length;

  const navigationSections = useMemo(
    () => [
      { label: "Principal", pages: ["dashboard", "queue"] },
      { label: "Clinico", pages: ["patients", "records", "admissions"] },
      { label: "Estrutura", pages: ["beds"] },
      { label: "Admin", pages: ["users"] }
    ],
    []
  );

  const onLogout = () => {
    logout.mutate(undefined, {
      onSettled: () => navigate("/login", { replace: true })
    });
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="sidebar-brand-icon">M</div>
        <div>
          <p className="sidebar-brand-title">MediCore</p>
          <p className="sidebar-brand-sub">HMS v1.0</p>
        </div>
      </div>

      <nav className="sidebar-nav">
        {navigationSections.map((section) => {
          const sectionItems = NAV_ITEMS.filter(
            (item) =>
              section.pages.includes(item.page) &&
              (!item.roles || item.roles.includes(session?.role ?? "RECEPTIONIST"))
          );

          if (sectionItems.length === 0) {
            return null;
          }

          return (
            <section key={section.label} className="sidebar-section">
              <p className="sidebar-section-title">{section.label}</p>
              {sectionItems.map((item) => {
                const path = pagePath[item.page];
                const active = location.pathname === path;

                return (
                  <Link key={item.page} className={`sidebar-nav-item ${active ? "active" : ""}`} to={path}>
                    <Icon name={item.icon} />
                    <span className="sidebar-nav-label">{item.label}</span>
                    {item.page === "admissions" && activeAdmissionsCount > 0 ? (
                      <span className="sidebar-nav-badge">{activeAdmissionsCount}</span>
                    ) : null}
                  </Link>
                );
              })}
            </section>
          );
        })}
      </nav>

      <footer className="sidebar-user">
        <div className="sidebar-avatar">{getInitials(session?.name ?? "MC")}</div>
        <div>
          <p className="sidebar-user-name">{session?.name ?? "Usuario"}</p>
          <p className="sidebar-user-role">{roleLabel[session?.role ?? ""] ?? "-"}</p>
        </div>
        <button className="sidebar-logout" onClick={onLogout} title="Sair" type="button">
          <Icon name="logout" />
        </button>
      </footer>
    </aside>
  );
}
