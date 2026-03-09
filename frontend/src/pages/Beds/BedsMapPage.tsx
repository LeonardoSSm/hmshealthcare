import { useMemo, useState } from "react";
import { useBeds } from "../../hooks/useBeds";
import type { BedStatus } from "../../types/bed.types";
import { PageCard } from "../../components/ui/PageCard";
import { StatusBadge } from "../../components/ui/StatusBadge";

const filters: Array<{ label: string; value: "ALL" | BedStatus }> = [
  { label: "Todos", value: "ALL" },
  { label: "Disponiveis", value: "AVAILABLE" },
  { label: "Ocupados", value: "OCCUPIED" },
  { label: "Higienizacao", value: "CLEANING" },
  { label: "Manutencao", value: "MAINTENANCE" }
];

const statusLabel: Record<BedStatus, string> = {
  AVAILABLE: "Disponivel",
  OCCUPIED: "Ocupado",
  CLEANING: "Higienizacao",
  MAINTENANCE: "Manutencao"
};

export default function BedsMapPage() {
  const [filter, setFilter] = useState<"ALL" | BedStatus>("ALL");
  const { data: beds = [], isLoading } = useBeds();

  const wardMap = useMemo(() => {
    const map = new Map<string, typeof beds>();
    beds.forEach((bed) => {
      if (filter !== "ALL" && bed.status !== filter) {
        return;
      }
      const current = map.get(bed.ward) ?? [];
      map.set(bed.ward, [...current, bed]);
    });
    return map;
  }, [beds, filter]);

  return (
    <div className="stack">
      <PageCard title="Filtros de leitos">
        <div className="chip-group">
          {filters.map((item) => {
            const count = item.value === "ALL" ? beds.length : beds.filter((bed) => bed.status === item.value).length;
            return (
              <button
                type="button"
                key={item.value}
                className={`chip ${filter === item.value ? "active" : ""}`}
                onClick={() => setFilter(item.value)}
              >
                {item.label} ({count})
              </button>
            );
          })}
        </div>
      </PageCard>

      {isLoading ? <p>Carregando leitos...</p> : null}

      {[...wardMap.entries()].map(([ward, wardBeds]) => {
        const occupiedCount = wardBeds.filter((bed) => bed.status === "OCCUPIED").length;
        return (
          <PageCard
            key={ward}
            title={ward}
            actions={
              <span className="muted">
                {occupiedCount}/{wardBeds.length} ocupados
              </span>
            }
          >
            <div className="beds-grid">
              {wardBeds.map((bed) => (
                <article key={bed.id} className={`bed-card ${bed.status.toLowerCase()}`}>
                  <header>
                    <strong>{bed.number}</strong>
                    <StatusBadge
                      tone={
                        bed.status === "AVAILABLE"
                          ? "success"
                          : bed.status === "OCCUPIED"
                            ? "warning"
                            : bed.status === "CLEANING"
                              ? "info"
                              : "danger"
                      }
                      label={statusLabel[bed.status]}
                    />
                  </header>
                  <p>{bed.type}</p>
                  <small>{bed.floor} andar</small>
                </article>
              ))}
            </div>
          </PageCard>
        );
      })}
    </div>
  );
}
