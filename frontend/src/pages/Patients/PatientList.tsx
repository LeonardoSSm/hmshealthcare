import { useMemo, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";
import type { Patient } from "../../types/patient.types";
import type { CreatePatientPayload } from "../../types/patient.types";
import { createPatient } from "../../services/patient.service";
import { usePatients } from "../../hooks/usePatients";
import { useToast } from "../../contexts/ToastContext";
import { PageCard } from "../../components/ui/PageCard";
import { Modal } from "../../components/ui/Modal";
import { StatusBadge } from "../../components/ui/StatusBadge";
import { extractApiErrorMessage } from "../../lib/apiError";
import { formatDate, getInitials } from "../../lib/format";

type StatusFilter = "ALL" | "ACTIVE" | "INACTIVE";

const bloodTypes = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

function onlyDigits(value: string): string {
  return value.replace(/\D/g, "");
}

function formatCpfInput(value: string): string {
  const digits = onlyDigits(value).slice(0, 11);

  if (digits.length <= 3) {
    return digits;
  }
  if (digits.length <= 6) {
    return `${digits.slice(0, 3)}.${digits.slice(3)}`;
  }
  if (digits.length <= 9) {
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`;
  }
  return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
}

function formatPhoneInput(value: string): string {
  const digits = onlyDigits(value).slice(0, 11);

  if (digits.length === 0) {
    return "";
  }
  if (digits.length <= 2) {
    return `(${digits}`;
  }

  const areaCode = digits.slice(0, 2);
  const localNumber = digits.slice(2);

  if (localNumber.length <= 4) {
    return `(${areaCode}) ${localNumber}`;
  }
  if (localNumber.length <= 8) {
    return `(${areaCode}) ${localNumber.slice(0, 4)}-${localNumber.slice(4)}`;
  }
  return `(${areaCode}) ${localNumber.slice(0, 5)}-${localNumber.slice(5)}`;
}

function isValidCpf(value: string): boolean {
  const digits = onlyDigits(value);
  if (digits.length !== 11 || new Set(digits).size === 1) {
    return false;
  }

  const calcDigit = (length: number, weightStart: number) => {
    let sum = 0;
    for (let i = 0; i < length; i += 1) {
      sum += Number(digits[i]) * (weightStart - i);
    }
    const remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  };

  return calcDigit(9, 10) === Number(digits[9]) && calcDigit(10, 11) === Number(digits[10]);
}

function calculateAge(birthDate: string): number {
  const now = new Date();
  const born = new Date(birthDate);
  if (Number.isNaN(born.getTime())) {
    return 0;
  }
  let age = now.getFullYear() - born.getFullYear();
  const monthDiff = now.getMonth() - born.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < born.getDate())) {
    age -= 1;
  }
  return Math.max(0, age);
}

interface NewPatientModalProps {
  onClose: () => void;
  onSubmit: (payload: CreatePatientPayload) => void;
  submitting: boolean;
}

function NewPatientModal({ onClose, onSubmit, submitting }: NewPatientModalProps) {
  const [form, setForm] = useState<CreatePatientPayload>({
    name: "",
    cpf: "",
    birthDate: "",
    bloodType: "A+",
    allergies: "",
    phone: "",
    email: "",
    address: ""
  });

  const setField = <K extends keyof CreatePatientPayload>(field: K, value: CreatePatientPayload[K]) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  return (
    <Modal
      title="Novo paciente"
      subtitle="Preencha os dados cadastrais"
      onClose={onClose}
      footer={
        <>
          <button type="submit" form="new-patient-form" className="btn-small" disabled={submitting}>
            {submitting ? "Salvando..." : "Cadastrar"}
          </button>
          <button type="button" className="btn-outline" onClick={onClose}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="new-patient-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit(form);
        }}
      >
        <label>
          Nome completo
          <input value={form.name} onChange={(event) => setField("name", event.target.value)} required />
        </label>
        <label>
          CPF
          <input
            value={form.cpf}
            onChange={(event) => setField("cpf", formatCpfInput(event.target.value))}
            inputMode="numeric"
            maxLength={14}
            placeholder="000.000.000-00"
            pattern="\d{3}\.\d{3}\.\d{3}-\d{2}"
            title="Use o formato 000.000.000-00"
            required
          />
        </label>
        <label>
          Data de nascimento
          <input
            type="date"
            value={form.birthDate}
            onChange={(event) => setField("birthDate", event.target.value)}
            required
          />
        </label>
        <label>
          Tipo sanguineo
          <select value={form.bloodType} onChange={(event) => setField("bloodType", event.target.value)} required>
            {bloodTypes.map((bloodType) => (
              <option key={bloodType} value={bloodType}>
                {bloodType}
              </option>
            ))}
          </select>
        </label>
        <label>
          Telefone
          <input
            value={form.phone}
            onChange={(event) => setField("phone", formatPhoneInput(event.target.value))}
            inputMode="numeric"
            maxLength={15}
            placeholder="(11) 99999-9999"
            pattern="\(\d{2}\)\s\d{4,5}-\d{4}"
            title="Use o formato (00) 00000-0000"
            required
          />
        </label>
        <label>
          Email
          <input type="email" value={form.email} onChange={(event) => setField("email", event.target.value)} required />
        </label>
        <label className="full-width">
          Endereco
          <input value={form.address} onChange={(event) => setField("address", event.target.value)} required />
        </label>
        <label className="full-width">
          Alergias
          <input
            value={form.allergies}
            onChange={(event) => setField("allergies", event.target.value)}
            placeholder="Informe alergias conhecidas"
          />
        </label>
      </form>
    </Modal>
  );
}

interface PatientDetailsModalProps {
  patient: Patient;
  onClose: () => void;
  onOpenRecord: () => void;
}

function PatientDetailsModal({ patient, onClose, onOpenRecord }: PatientDetailsModalProps) {
  return (
    <Modal
      title={patient.name}
      subtitle={`${patient.id} - ${patient.cpf}`}
      onClose={onClose}
      footer={
        <>
          <button type="button" className="btn-small" onClick={onOpenRecord}>
            Ver prontuario
          </button>
          <button type="button" className="btn-outline" onClick={onClose}>
            Fechar
          </button>
        </>
      }
    >
      <div className="patient-modal-header">
        <span className="patient-modal-avatar">{getInitials(patient.name)}</span>
        <div>
          <p className="patient-modal-name">{patient.name}</p>
          <p className="patient-modal-muted">{patient.email}</p>
        </div>
      </div>
      <div className="detail-grid">
        <div>
          <p className="detail-label">Nascimento</p>
          <p className="detail-value">{formatDate(patient.birthDate)}</p>
        </div>
        <div>
          <p className="detail-label">Idade</p>
          <p className="detail-value">{calculateAge(patient.birthDate)} anos</p>
        </div>
        <div>
          <p className="detail-label">Tipo sanguineo</p>
          <p className="detail-value">{patient.bloodType}</p>
        </div>
        <div>
          <p className="detail-label">Telefone</p>
          <p className="detail-value">{patient.phone}</p>
        </div>
        <div className="full-width">
          <p className="detail-label">Endereco</p>
          <p className="detail-value">{patient.address}</p>
        </div>
        <div className="full-width">
          <p className="detail-label">Alergias</p>
          <p className="detail-value">{patient.allergies || "Nao informado"}</p>
        </div>
      </div>
    </Modal>
  );
}

export default function PatientListPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { notify } = useToast();
  const session = useAuthStore((state) => state.session);
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState<StatusFilter>("ALL");
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const { data = [], isLoading, isError } = usePatients(query);

  const createPatientMutation = useMutation({
    mutationFn: createPatient,
    onSuccess: async () => {
      setShowCreateModal(false);
      await queryClient.invalidateQueries({ queryKey: ["patients"] });
      notify("Paciente cadastrado com sucesso.");
    },
    onError: (error) => {
      notify(extractApiErrorMessage(error, "Nao foi possivel cadastrar o paciente."), "danger");
    }
  });

  const filteredPatients = useMemo(
    () => data.filter((patient) => (status === "ALL" ? true : patient.status === status)),
    [data, status]
  );
  const canCreatePatient = session?.role === "ADMIN" || session?.role === "RECEPTIONIST";

  return (
    <div className="stack">
      {showCreateModal ? (
        <NewPatientModal
          onClose={() => setShowCreateModal(false)}
          onSubmit={(payload) => {
            const cpfDigits = onlyDigits(payload.cpf);
            const phoneDigits = onlyDigits(payload.phone);

            if (!isValidCpf(cpfDigits)) {
              notify("CPF invalido. Use um CPF valido no formato 000.000.000-00.", "danger");
              return;
            }
            if (phoneDigits.length < 10 || phoneDigits.length > 11) {
              notify("Telefone invalido. Use (00) 00000-0000.", "danger");
              return;
            }

            createPatientMutation.mutate(payload);
          }}
          submitting={createPatientMutation.isPending}
        />
      ) : null}

      {selectedPatient ? (
        <PatientDetailsModal
          patient={selectedPatient}
          onClose={() => setSelectedPatient(null)}
          onOpenRecord={() => navigate(`/records?patientId=${selectedPatient.id}`)}
        />
      ) : null}

      <PageCard
        actions={
          <button
            type="button"
            className="btn-small"
            disabled={!canCreatePatient}
            title={!canCreatePatient ? "Somente ADMIN/RECEPCAO podem cadastrar pacientes." : undefined}
            onClick={() => {
              if (!canCreatePatient) {
                notify("Seu perfil nao possui permissao para cadastrar pacientes.", "warning");
                return;
              }
              setShowCreateModal(true);
            }}
          >
            + Novo paciente
          </button>
        }
      >
        <div className="search-wrap">
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome ou CPF"
            className="search-input"
          />
          <select
            className="search-select"
            value={status}
            onChange={(event) => setStatus(event.target.value as StatusFilter)}
          >
            <option value="ALL">Todos os status</option>
            <option value="ACTIVE">Ativos</option>
            <option value="INACTIVE">Inativos</option>
          </select>
        </div>
      </PageCard>

      <PageCard title="Lista de pacientes">
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Paciente</th>
                <th>CPF</th>
                <th>Idade</th>
                <th>Tipo sanguineo</th>
                <th>Status</th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan={6}>Carregando...</td>
                </tr>
              ) : null}

              {isError ? (
                <tr>
                  <td colSpan={6}>Erro ao buscar pacientes.</td>
                </tr>
              ) : null}

              {!isLoading && !isError && filteredPatients.length === 0 ? (
                <tr>
                  <td colSpan={6}>Nenhum paciente encontrado.</td>
                </tr>
              ) : null}

              {filteredPatients.map((patient) => (
                <tr key={patient.id}>
                  <td>
                    <div className="user-inline">
                      <span className="user-avatar">{getInitials(patient.name)}</span>
                      <span>{patient.name}</span>
                    </div>
                  </td>
                  <td>{patient.cpf}</td>
                  <td>{calculateAge(patient.birthDate)} anos</td>
                  <td>{patient.bloodType}</td>
                  <td>
                    <StatusBadge
                      tone={patient.status === "ACTIVE" ? "success" : "neutral"}
                      label={patient.status === "ACTIVE" ? "Ativo" : "Inativo"}
                    />
                  </td>
                  <td>
                    <div className="inline-actions">
                      <button type="button" className="btn-outline" onClick={() => setSelectedPatient(patient)}>
                        Ver
                      </button>
                      <button
                        type="button"
                        className="btn-small"
                        onClick={() => navigate(`/records?patientId=${patient.id}`)}
                      >
                        Prontuario
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </PageCard>
    </div>
  );
}
