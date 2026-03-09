import { Outlet } from "react-router-dom";
import { useEffect, useState } from "react";
import { Sidebar } from "./Sidebar";
import { Topbar } from "./Topbar";

const storageKey = "medicore-theme-dark";

export function AppShell() {
  const [darkMode, setDarkMode] = useState(() => localStorage.getItem(storageKey) === "true");

  useEffect(() => {
    localStorage.setItem(storageKey, String(darkMode));
    document.documentElement.setAttribute("data-theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  return (
    <div className="app-shell">
      <Sidebar />
      <main className="app-main">
        <Topbar darkMode={darkMode} onToggleDarkMode={() => setDarkMode((value) => !value)} />
        <div className="page-container">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
