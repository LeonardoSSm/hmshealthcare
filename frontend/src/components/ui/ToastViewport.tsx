export type ToastTone = "success" | "info" | "warning" | "danger";

export interface ToastMessage {
  id: string;
  message: string;
  tone: ToastTone;
}

interface ToastViewportProps {
  toasts: ToastMessage[];
  onDismiss: (id: string) => void;
}

const toneLabel: Record<ToastTone, string> = {
  success: "Sucesso",
  info: "Info",
  warning: "Aviso",
  danger: "Erro"
};

export function ToastViewport({ toasts, onDismiss }: ToastViewportProps) {
  return (
    <div className="toast-viewport" aria-live="polite" aria-atomic="true">
      {toasts.map((toast) => (
        <div className={`toast toast-${toast.tone}`} key={toast.id}>
          <span className={`toast-dot toast-dot-${toast.tone}`} aria-hidden />
          <div className="toast-content">
            <span className="toast-label">{toneLabel[toast.tone]}</span>
            <p>{toast.message}</p>
          </div>
          <button type="button" className="toast-close" onClick={() => onDismiss(toast.id)}>
            ×
          </button>
        </div>
      ))}
    </div>
  );
}
