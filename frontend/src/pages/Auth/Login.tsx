import type { FormEvent } from "react";
import { useState } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { Icon } from "../../components/ui/Icon";

const testAccount = {
  name: "Admin User",
  role: "ADMIN",
  email: "admin@medicore.local",
  password: "admin123"
};

export default function LoginPage() {
  const { session, login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  if (session) {
    return <Navigate to="/dashboard" replace />;
  }

  const onSubmit = (event: FormEvent) => {
    event.preventDefault();
    login.mutate({ email, password });
  };

  return (
    <main className="login-root">
      <section className="login-left">
        <div className="login-brand">
          <div className="login-brand-icon">M</div>
          <div>
            <h1>MediCore HMS</h1>
            <p>Hospital Management Platform</p>
          </div>
        </div>
        <p className="login-left-copy">
          Frontend modular em TypeScript com autenticacao JWT e areas funcionais para operacao hospitalar.
        </p>
      </section>

      <section className="login-right">
        <h2>Acesso seguro</h2>
        <p>Entre com suas credenciais para continuar.</p>

        <form onSubmit={onSubmit}>
          <label>Email</label>
          <input
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            type="email"
            className="form-input"
            placeholder="admin@medicore.local"
            autoComplete="username"
            required
          />

          <label>Senha</label>
          <input
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            type="password"
            className="form-input"
            autoComplete="current-password"
            placeholder="Sua senha"
            required
          />

          {login.isError ? (
            <p className="login-error">Falha no login. Verifique email e senha.</p>
          ) : null}

          <button type="submit" className="btn-primary" disabled={login.isPending}>
            {login.isPending ? "Entrando..." : "Entrar"}
          </button>
        </form>

        <button
          className="login-helper"
          type="button"
          onClick={() => {
            setEmail(testAccount.email);
            setPassword(testAccount.password);
          }}
        >
          <Icon name="user" /> Preencher conta de teste
        </button>

        <div className="login-test-list">
          <p>Conta de desenvolvimento</p>
          <button
            type="button"
            className="login-test-card"
            onClick={() => {
              setEmail(testAccount.email);
              setPassword(testAccount.password);
            }}
          >
            <span className="login-test-role">{testAccount.role}</span>
            <span>{testAccount.name}</span>
            <small>{testAccount.email}</small>
          </button>
        </div>
      </section>
    </main>
  );
}
