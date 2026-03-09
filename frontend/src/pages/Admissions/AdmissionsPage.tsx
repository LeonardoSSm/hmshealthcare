import { useMemo, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { usePatients } from "../../hooks/usePatients";
import { useAdmissions } from "../../hooks/useAdmissions";
import { useBeds } from "../../hooks/useBeds";
import { useDoctors } from "../../hooks/useUsers";
import { useToast } from "../../contexts/ToastContext";
import { StatCard } from "../../components/ui/StatCard";
import { PageCard } from "../../components/ui/PageCard";
import { Modal } from "../../components/ui/Modal";
import { StatusBadge } from "../../components/ui/StatusBadge";
import { formatDateTime } from "../../lib/format";
import { extractApiErrorMessage } from "../../lib/apiError";
import { admitPatientRemote, dischargeAdmissionRemote } from "../../services/admission.service";

interface AdmitFormState {
  patientId: string;
  bedId: string;
  doctorId: string;
  reason: string;
}

interface AdmitPatientModalProps {
  form: AdmitFormState;
  onChange: (next: AdmitFormState) => void;
  onConfirm: () => void;
  onClose: () => void;
  patients: Array<{ id: string; name: string }>;
  availableBeds: Array<{ id: string; number: string; ward: string }>;
  doctors: Array<{ id: string; name: string }>;
  pending: boolean;
}

function AdmitPatientModal({
  form,
  onChange,
  onConfirm,
  onClose,
  patients,
  availableBeds,
  doctors,
  pending
}: AdmitPatientModalProps) {
  const setField = <K extends keyof AdmitFormState>(field: K, value: AdmitFormState[K]) => {
    onChange({ ...form, [field]: value });
  };

  return (
    <Modal
      title="Admitir paciente"
      subtitle="Registrar nova internacao hospitalar"
      onClose={onClose}
      footer={
        <>
          <button type="submit" form="admit-patient-form" className="btn-small" disabled={pending}>
            {pending ? "Salvando..." : "Confirmar admissao"}
          </button>
          <button type="button" className="btn-outline" onClick={onClose} disabled={pending}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="admit-patient-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onConfirm();
        }}
      >
        <label className="full-width">
          Paciente
          <select value={form.patientId} onChange={(event) => setField("patientId", event.target.value)} required>
            <option value="">Selecione</option>
            {patients.map((patient) => (
              <option key={patient.id} value={patient.id}>
                {patient.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Leito
          <select value={form.bedId} onChange={(event) => setField("bedId", event.target.value)} required>
            <option value="">Selecione</option>
            {availableBeds.map((bed) => (
              <option key={bed.id} value={bed.id}>
                {bed.number} - {bed.ward}
              </option>
            ))}
          </select>
        </label>
        <label>
          Medico responsavel
          <select value={form.doctorId} onChange={(event) => setField("doctorId", event.target.value)} required>
            <option value="">Selecione</option>
            {doctors.map((doctor) => (
              <option key={doctor.id} value={doctor.id}>
                {doctor.name}
              </option>
            ))}
          </select>
        </label>
        <label className="full-width">
          Motivo da internacao
          <textarea
            rows={3}
            value={form.reason}
            onChange={(event) => setField("reason", event.target.value)}
            required
          />
        </label>
      </form>
    </Modal>
  );
}

function getStayDays(start: string, end: string | null): number {
  const startDate = new Date(start);
  const endDate = end ? new Date(end) : new Date();
  return Math.max(1, Math.ceil((endDate.getTime() - startDate.getTime()) / 86_400_000));
}

export default function AdmissionsPage() {
  const { notify } = useToast();
  const queryClient = useQueryClient();
  const [showAdmitModal, setShowAdmitModal] = useState(false);
  const [search, setSearch] = useState("");
  const [admitForm, setAdmitForm] = useState<AdmitFormState>({
    patientId: "",
    bedId: "",
    doctorId: "",
    reason: ""
  });

  const { data: patients = [] } = usePatients("");
  const { data: admissions = [], isLoading: loadingAdmissions } = useAdmissions();
  const { data: beds = [] } = useBeds();
  const { data: doctors = [] } = useDoctors();

  const patientNameById = useMemo(() => new Map(patients.map((patient) => [patient.id, patient.name])), [patients]);
  const doctorNameById = useMemo(() => new Map(doctors.map((doctor) => [doctor.id, doctor.name])), [doctors]);
  const bedById = useMemo(() => new Map(beds.map((bed) => [bed.id, bed])), [beds]);

  const admitRemoteMutation = useMutation({
    mutationFn: admitPatientRemote,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admissions"] });
      await queryClient.invalidateQueries({ queryKey: ["beds"] });
      setShowAdmitModal(false);
      setAdmitForm({ patientId: "", bedId: "", doctorId: "", reason: "" });
      notify("Paciente admitido com sucesso.");
    },
    onError: (error) => {
      notify(extractApiErrorMessage(error, "Nao foi possivel salvar internacao no backend."), "danger");
    }
  });

  const dischargeRemoteMutation = useMutation({
    mutationFn: dischargeAdmissionRemote,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admissions"] });
      await queryClient.invalidateQueries({ queryKey: ["beds"] });
      notify("Alta registrada com sucesso.");
    },
    onError: (error) => {
      notify(extractApiErrorMessage(error, "Nao foi possivel registrar alta no backend."), "danger");
    }
  });

  const availableBeds = useMemo(
    () =>
      beds
        .filter((bed) => bed.status === "AVAILABLE")
        .map((bed) => ({ id: bed.id, number: bed.number, ward: bed.ward })),
    [beds]
  );

  const admitCandidates = useMemo(
    () =>
      patients
        .filter(
          (patient) =>
            patient.status === "ACTIVE" &&
            !admissions.some((admission) => admission.patientId === patient.id && admission.status === "ACTIVE")
        )
        .map((patient) => ({ id: patient.id, name: patient.name })),
    [admissions, patients]
  );

  const rows = useMemo(
    () =>
      admissions.map((admission) => {
        const patientName = patientNameById.get(admission.patientId) ?? admission.patientId;
        const doctorName = doctorNameById.get(admission.doctorId) ?? admission.doctorId;
        const bed = bedById.get(admission.bedId);
        const bedLabel = bed ? bed.number : admission.bedId;
        return { admission, patientName, doctorName, bedLabel };
      }),
    [admissions, bedById, doctorNameById, patientNameById]
  );

  const filteredRows = useMemo(
    () =>
      rows.filter(({ admission, patientName, doctorName, bedLabel }) => {
        if (!search.trim()) {
          return true;
        }
        const term = search.trim().toLowerCase();
        return (
          admission.id.toLowerCase().includes(term) ||
          patientName.toLowerCase().includes(term) ||
          doctorName.toLowerCase().includes(term) ||
          bedLabel.toLowerCase().includes(term) ||
          admission.reason.toLowerCase().includes(term)
        );
      }),
    [rows, search]
  );

  const activeAdmissions = admissions.filter((admission) => admission.status === "ACTIVE").length;
  const dischargedAdmissions = admissions.filter((admission) => admission.status === "DISCHARGED").length;
  const availableBedsCount = beds.filter((bed) => bed.status === "AVAILABLE").length;

  const handleAdmit = () => {
    if (!admitForm.patientId || !admitForm.bedId || !admitForm.doctorId || !admitForm.reason.trim()) {
      notify("Preencha paciente, leito, medico e motivo da internacao.", "warning");
      return;
    }

    admitRemoteMutation.mutate({
      patientId: admitForm.patientId,
      bedId: admitForm.bedId,
      doctorId: admitForm.doctorId,
      reason: admitForm.reason.trim()
    });
  };

  return (
    <div className="stack">
      {showAdmitModal ? (
        <AdmitPatientModal
          form={admitForm}
          onChange={setAdmitForm}
          onConfirm={handleAdmit}
          onClose={() => setShowAdmitModal(false)}
          patients={admitCandidates}
          availableBeds={availableBeds}
          doctors={doctors.map((doctor) => ({ id: doctor.id, name: doctor.name }))}
          pending={admitRemoteMutation.isPending}
        />
      ) : null}

      <section className="stats-grid">
        <StatCard title="Internacoes ativas" value={activeAdmissions} tone="amber" icon={<span>IN</span>} />
        <StatCard title="Altas realizadas" value={dischargedAdmissions} tone="green" icon={<span>AL</span>} />
        <StatCard title="Leitos disponiveis" value={availableBedsCount} tone="blue" icon={<span>LT</span>} />
        <StatCard title="Total de internacoes" value={admissions.length} tone="teal" icon={<span>TT</span>} />
      </section>

      <PageCard
        title="Registro de internacoes"
        actions={
          <button type="button" className="btn-small" onClick={() => setShowAdmitModal(true)}>
            + Admitir paciente
          </button>
        }
      >
        <div className="search-wrap">
          <input
            className="search-input"
            placeholder="Buscar por paciente, leito, motivo ou ID"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>

        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Paciente</th>
                <th>Leito</th>
                <th>Medico</th>
                <th>Motivo</th>
                <th>Admissao</th>
                <th>Dias</th>
                <th>Status</th>
                <th>Acao</th>
              </tr>
            </thead>
            <tbody>
              {loadingAdmissions ? (
                <tr>
                  <td colSpan={9}>Carregando internacoes...</td>
                </tr>
              ) : null}

              {!loadingAdmissions
                ? filteredRows.map(({ admission, patientName, doctorName, bedLabel }) => (
                    <tr key={admission.id}>
                      <td>{admission.id.slice(0, 8).toUpperCase()}</td>
                      <td>{patientName}</td>
                      <td>{bedLabel}</td>
                      <td>{doctorName}</td>
                      <td>{admission.reason}</td>
                      <td>{formatDateTime(admission.admissionDate)}</td>
                      <td>{getStayDays(admission.admissionDate, admission.dischargeDate)}d</td>
                      <td>
                        <StatusBadge
                          tone={admission.status === "ACTIVE" ? "warning" : "success"}
                          label={admission.status === "ACTIVE" ? "Em andamento" : "Alta"}
                        />
                      </td>
                      <td>
                        {admission.status === "ACTIVE" ? (
                          <button
                            type="button"
                            className="btn-outline"
                            disabled={dischargeRemoteMutation.isPending}
                            onClick={() => dischargeRemoteMutation.mutate(admission.id)}
                          >
                            Alta
                          </button>
                        ) : (
                          <span className="muted">-</span>
                        )}
                      </td>
                    </tr>
                  ))
                : null}

              {!loadingAdmissions && filteredRows.length === 0 ? (
                <tr>
                  <td colSpan={9}>Nenhum registro encontrado.</td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </PageCard>
    </div>
  );
}
