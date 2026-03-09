import axios from "axios";

interface ErrorPayload {
  code?: string;
  status?: number;
  error?: string;
  message?: string;
  details?: Record<string, unknown>;
  [key: string]: unknown;
}

export function extractApiErrorMessage(error: unknown, fallback: string): string {
  if (!axios.isAxiosError(error)) {
    return fallback;
  }

  const status = error.response?.status;
  const payload = error.response?.data as ErrorPayload | undefined;
  if (typeof payload?.error === "string" && payload.error.trim()) {
    return payload.error;
  }
  if (typeof payload?.message === "string" && payload.message.trim()) {
    return payload.message;
  }

  if (payload?.details && typeof payload.details === "object") {
    const firstDetail = Object.values(payload.details).find(
      (value) => typeof value === "string" && value.trim().length > 0
    );
    if (typeof firstDetail === "string") {
      return firstDetail;
    }
  }

  if (status === 403) {
    return "Acesso negado (403). Seu perfil nao possui permissao para essa acao.";
  }
  if (status === 401) {
    return "Sessao expirada. Entre novamente.";
  }
  if (status === 409) {
    return "Conflito de dados. Verifique valores unicos ou estado atual do registro.";
  }

  if (status === 422 && payload) {
    const fieldMessage = Object.entries(payload).find(
      ([, value]) => typeof value === "string" && String(value).trim()
    );
    if (fieldMessage) {
      return String(fieldMessage[1]);
    }
  }

  return fallback;
}
