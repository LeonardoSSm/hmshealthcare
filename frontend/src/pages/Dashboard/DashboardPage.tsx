import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { usePatients } from "../../hooks/usePatients";
import { useAdmissions } from "../../hooks/useAdmissions";
import { useBeds } from "../../hooks/useBeds";
import { useDoctors } from "../../hooks/useUsers";
import { StatCard } from "../../components/ui/StatCard";
import { PageCard } from "../../components/ui/PageCard";
import { StatusBadge } from "../../components/ui/StatusBadge";
import { Icon } from "../../components/ui/Icon";

function getStayDays(start: string, end: string | null): number {
  const startDate = new Date(start);
  const endDate = end ? new Date(end) : new Date();
  const diffMs = endDate.getTime() - startDate.getTime();
  return Math.max(1, Math.ceil(diffMs / 86_400_000));
}

export default function DashboardPage() {
  const navigate = useNavigate();
  const { data: patients = [], isLoading: loadingPatients } = usePatients("");
  const { data: admissions = [] } = useAdmissions();
  const { data: beds = [] } = useBeds();
  const { data: doctors = [] } = useDoctors();

  const patientNameById = useMemo(() => new Map(patients.map((patient) => [patient.id, patient.name])), [patients]);
  const doctorNameById = useMemo(() => new Map(doctors.map((doctor) => [doctor.id, doctor.name])), [doctors]);
  const bedNumberById = useMemo(() => new Map(beds.map((bed) => [bed.id, bed.number])), [beds]);

  const activeAdmissions = useMemo(
    () => admissions.filter((admission) => admission.status === "ACTIVE"),
    [admissions]
  );

  const occupiedBeds = beds.filter((bed) => bed.status === "OCCUPIED").length;
  const availableBeds = beds.filter((bed) => bed.status === "AVAILABLE").length;
  const cleaningBeds = beds.filter((bed) => bed.status === "CLEANING").length;
  const maintenanceBeds = beds.filter((bed) => bed.status === "MAINTENANCE").length;
  const occupancyRate = beds.length === 0 ? 0 : Math.round((occupiedBeds / beds.length) * 100);

  return (
    <div className="stack">
      <section className="stats-grid">
        <StatCard
          title="Pacientes Ativos"
          value={loadingPatients ? "..." : patients.filter((patient) => patient.status === "ACTIVE").length}
          trend="+ 3 esta semana"
          trendTone="up"
          tone="blue"
          icon={<Icon name="team" />}
        />
        <StatCard
          title="Internados Agora"
          value={activeAdmissions.length}
          trend="+ 1 hoje"
          trendTone="up"
          tone="amber"
          icon={<Icon name="hospital" />}
        />
        <StatCard
          title="Leitos Disponiveis"
          value={availableBeds}
          trend="- 2 vs ontem"
          trendTone="down"
          tone="green"
          icon={<Icon name="bed" />}
        />
        <StatCard
          title="Altas Hoje"
          value={admissions.filter((admission) => admission.status === "DISCHARGED").length}
          trend="+ 1 vs ontem"
          trendTone="up"
          tone="teal"
          icon={<Icon name="file" />}
        />
      </section>

      <section className="dashboard-grid">
        <PageCard
          title="Internacoes Ativas"
          actions={
            <button type="button" className="btn-outline" onClick={() => navigate("/admissions")}>
              Ver todas
            </button>
          }
        >
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Paciente</th>
                  <th>Leito</th>
                  <th>Medico</th>
                  <th>Dias</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {activeAdmissions.slice(0, 6).map((admission) => (
                  <tr key={admission.id}>
                    <td>{patientNameById.get(admission.patientId) ?? admission.patientId}</td>
                    <td>
                      <span className="table-chip">{bedNumberById.get(admission.bedId) ?? admission.bedId}</span>
                    </td>
                    <td className="cell-muted">{doctorNameById.get(admission.doctorId) ?? admission.doctorId}</td>
                    <td className={getStayDays(admission.admissionDate, admission.dischargeDate) > 7 ? "cell-alert" : ""}>
                      {getStayDays(admission.admissionDate, admission.dischargeDate)}d
                    </td>
                    <td>
                      <StatusBadge tone="warning" label="Internado" />
                    </td>
                  </tr>
                ))}
                {activeAdmissions.length === 0 ? (
                  <tr>
                    <td colSpan={5}>Nenhuma internacao ativa.</td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </div>
        </PageCard>

        <div className="stack">
          <PageCard title="Ocupacao de leitos">
            <div className="occupancy-head">
              <strong>{occupancyRate}%</strong>
              <span>
                {occupiedBeds}/{beds.length} leitos
              </span>
            </div>
            <div className="occupancy-bar">
              <span style={{ width: `${occupancyRate}%` }} />
            </div>
            <div className="occupancy-grid">
              <article>
                <strong>{occupiedBeds}</strong>
                <p>Ocupados</p>
              </article>
              <article>
                <strong>{availableBeds}</strong>
                <p>Disponiveis</p>
              </article>
              <article>
                <strong>{cleaningBeds}</strong>
                <p>Higienizacao</p>
              </article>
              <article>
                <strong>{maintenanceBeds}</strong>
                <p>Manutencao</p>
              </article>
            </div>
          </PageCard>

          <PageCard title="Acoes rapidas">
            <div className="quick-actions">
              <button type="button" className="btn-outline" onClick={() => navigate("/patients")}>
                <Icon name="user" /> Novo Paciente
              </button>
              <button type="button" className="btn-outline" onClick={() => navigate("/admissions")}>
                <Icon name="hospital" /> Nova Internacao
              </button>
              <button type="button" className="btn-outline" onClick={() => navigate("/queue")}>
                <Icon name="queue" /> Fila de Atendimento
              </button>
              <button type="button" className="btn-outline" onClick={() => navigate("/beds")}>
                <Icon name="bed" /> Ver Leitos
              </button>
            </div>
          </PageCard>
        </div>
      </section>
    </div>
  );
}
