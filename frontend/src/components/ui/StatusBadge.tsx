interface StatusBadgeProps {
  label: string;
  tone: "success" | "warning" | "neutral" | "danger" | "info";
}

export function StatusBadge({ label, tone }: StatusBadgeProps) {
  return <span className={`status-badge ${tone}`}>{label}</span>;
}

