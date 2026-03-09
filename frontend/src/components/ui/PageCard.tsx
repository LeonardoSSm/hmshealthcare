import type { PropsWithChildren, ReactNode } from "react";

interface PageCardProps extends PropsWithChildren {
  title?: string;
  actions?: ReactNode;
}

export function PageCard({ title, actions, children }: PageCardProps) {
  return (
    <section className="page-card">
      {title || actions ? (
        <header className="page-card-header">
          {title ? <h2>{title}</h2> : <span />}
          {actions}
        </header>
      ) : null}
      <div className="page-card-body">{children}</div>
    </section>
  );
}

