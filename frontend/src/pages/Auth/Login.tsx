import type { FormEvent } from "react";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

export default function LoginPage() {
  const { session, login } = useAuth();
  const [email, setEmail] = useState("admin@medicore.local");
  const [password, setPassword] = useState("admin123");

  if (session) {
    return <Navigate to="/patients" replace />;
  }

  const onSubmit = (event: FormEvent) => {
    event.preventDefault();
    login.mutate({ email, password });
  };

  return (
    <main className="grid min-h-screen place-items-center bg-gradient-to-br from-brand-50 via-white to-emerald-100 p-6">
      <form onSubmit={onSubmit} className="w-full max-w-md rounded-2xl bg-white p-8 shadow-xl">
        <h1 className="text-2xl font-bold text-brand-700">MediCore HMS</h1>
        <p className="mt-1 text-sm text-slate-500">Acesse com suas credenciais.</p>

        <label className="mt-6 block text-sm font-medium text-slate-700">Email</label>
        <input
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          type="email"
          className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
          required
        />

        <label className="mt-4 block text-sm font-medium text-slate-700">Senha</label>
        <input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          type="password"
          className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
          required
        />

        {login.isError && (
          <p className="mt-3 text-sm text-red-600">Falha no login. Verifique as credenciais.</p>
        )}

        <button
          type="submit"
          className="mt-6 w-full rounded-lg bg-brand-700 px-4 py-2 font-semibold text-white hover:bg-brand-900 disabled:opacity-60"
          disabled={login.isPending}
        >
          {login.isPending ? "Entrando..." : "Entrar"}
        </button>
      </form>
    </main>
  );
}
