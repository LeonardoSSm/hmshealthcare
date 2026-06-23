import { Component } from "react";
import type { ErrorInfo, ReactNode } from "react";

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error("[ErrorBoundary]", error, info.componentStack);
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="error-boundary-page">
          <div className="error-boundary-card">
            <h1 className="error-boundary-code">Algo deu errado</h1>
            <p className="error-boundary-message">
              {this.state.error?.message ?? "Ocorreu um erro inesperado. Tente recarregar a pagina."}
            </p>
            <button
              type="button"
              className="btn-small"
              onClick={() => window.location.reload()}
            >
              Recarregar pagina
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
