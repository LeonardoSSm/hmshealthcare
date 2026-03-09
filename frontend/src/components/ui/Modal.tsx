import type { PropsWithChildren, ReactNode } from "react";

interface ModalProps extends PropsWithChildren {
  title: string;
  subtitle?: string;
  onClose: () => void;
  footer?: ReactNode;
  overlayClassName?: string;
  className?: string;
  headerClassName?: string;
  bodyClassName?: string;
  footerClassName?: string;
  closeClassName?: string;
}

export function Modal({
  title,
  subtitle,
  onClose,
  footer,
  overlayClassName,
  className,
  headerClassName,
  bodyClassName,
  footerClassName,
  closeClassName,
  children
}: ModalProps) {
  return (
    <div className={`modal-overlay ${overlayClassName ?? ""}`.trim()} onClick={onClose} role="presentation">
      <div
        className={`modal ${className ?? ""}`.trim()}
        onClick={(event) => event.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-label={title}
      >
        <button
          type="button"
          className={`modal-close ${closeClassName ?? ""}`.trim()}
          onClick={onClose}
          aria-label="Fechar"
        >
          x
        </button>
        <header className={`modal-header ${headerClassName ?? ""}`.trim()}>
          <h3 className="modal-title">{title}</h3>
          {subtitle ? <p className="modal-subtitle">{subtitle}</p> : null}
        </header>
        <div className={`modal-body ${bodyClassName ?? ""}`.trim()}>{children}</div>
        {footer ? <footer className={`modal-footer ${footerClassName ?? ""}`.trim()}>{footer}</footer> : null}
      </div>
    </div>
  );
}
