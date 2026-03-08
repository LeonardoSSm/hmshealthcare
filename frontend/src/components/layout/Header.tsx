import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

export function Header({ title }: { title: string }) {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const onLogout = () => {
    logout.mutate(undefined, {
      onSettled: () => navigate("/login", { replace: true })
    });
  };

  return (
    <header className="border-b border-slate-200 bg-white">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4">
        <h1 className="text-lg font-semibold text-slate-900">{title}</h1>
        <button onClick={onLogout} className="text-sm text-brand-700 hover:underline">
          Sair
        </button>
      </div>
    </header>
  );
}
