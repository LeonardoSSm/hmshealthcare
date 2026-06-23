import { useNavigate } from "react-router-dom";

export default function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div className="error-boundary-page">
      <div className="error-boundary-card">
        <h1 className="error-boundary-code">404</h1>
        <p className="error-boundary-message">Pagina nao encontrada.</p>
        <button type="button" className="btn-small" onClick={() => navigate("/dashboard", { replace: true })}>
          Ir para o inicio
        </button>
      </div>
    </div>
  );
}
