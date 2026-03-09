import type { ReactNode } from "react";

interface StatCardProps {
  title: string;
  value: string | number;
  trend?: string;
  trendTone?: "up" | "down" | "neutral";
  tone: "blue" | "green" | "amber" | "teal";
  icon?: ReactNode;
}

export function StatCard({ title, value, trend, trendTone = "neutral", tone, icon }: StatCardProps) {
  return (
    <article className={`stat-card ${tone}`}>
      <span className="stat-card-icon">{icon}</span>
      <strong className="stat-card-value">{value}</strong>
      <p className="stat-card-title">{title}</p>
      {trend ? <p className={`stat-card-trend ${trendTone}`}>{trend}</p> : null}
    </article>
  );
}
