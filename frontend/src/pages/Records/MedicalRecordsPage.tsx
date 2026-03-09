import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useSearchParams } from "react-router-dom";
import { PageCard } from "../../components/ui/PageCard";
import { Modal } from "../../components/ui/Modal";
import { Icon } from "../../components/ui/Icon";
import type { IconName } from "../../components/ui/Icon";
import { usePatients } from "../../hooks/usePatients";
import { useAddMedicalRecordEvent, useMedicalRecord } from "../../hooks/useMedicalRecord";
import { getPatientById } from "../../services/patient.service";
import { useAuthStore } from "../../store/authStore";
import { useToast } from "../../contexts/ToastContext";
import { formatDateTime, getInitials } from "../../lib/format";

type EventType = "ADMISSION" | "DIAGNOSIS" | "PRESCRIPTION" | "OBSERVATION";

interface ClinicalEvent {
  id: string;
  date: string;
  type: EventType;
  author: string;
  content: string;
}

const eventLabel: Record<EventType, string> = {
  ADMISSION: "Admissão",
  DIAGNOSIS: "Diagnóstico",
  PRESCRIPTION: "Prescrição",
  OBSERVATION: "Observação"
};

const eventIcon: Record<EventType, IconName> = {
  ADMISSION: "admission",
  DIAGNOSIS: "diagnosis",
  PRESCRIPTION: "prescription",
  OBSERVATION: "observation"
};

const eventTypes = Object.keys(eventLabel) as EventType[];

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

function formatCpf(value: string): string {
  const digits = value.replace(/\D/g, "");

  if (digits.length !== 11) {
    return value;
  }

  return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
}

function formatPatientTag(patientId: string): string {
  const normalized = patientId.replace(/[^a-zA-Z0-9]/g, "").toUpperCase();

  if (normalized.startsWith("P") && normalized.length >= 5) {
    return `${normalized.slice(0, 1)}-${normalized.slice(1, 5)}`;
  }

  const suffix = normalized.slice(-4).padStart(4, "0");
  return `P-${suffix}`;
}

interface AddEventModalProps {
  onClose: () => void;
  onSave: (event: {
    type: EventType;
    date: string;
    description: string;
    notes: string;
    icd10Code: string;
  }) => void;
  pending: boolean;
}

function AddEventModal({ onClose, onSave, pending }: AddEventModalProps) {
  const [type, setType] = useState<EventType>("DIAGNOSIS");
  const [date, setDate] = useState(() => new Date().toISOString().slice(0, 16));
  const [description, setDescription] = useState("");
  const [icd10Code, setIcd10Code] = useState("");

  return (
    <Modal
      title="Novo Registro Clínico"
      subtitle="Adicionar evento ao prontuário do paciente"
      onClose={onClose}
      overlayClassName="records-event-modal-overlay"
      className="records-event-modal"
      headerClassName="records-event-modal-header"
      bodyClassName="records-event-modal-body"
      footerClassName="records-event-modal-footer"
      closeClassName="records-event-modal-close"
      footer={
        <>
          <button
            type="submit"
            form="add-record-event-form"
            className="records-event-submit"
            disabled={pending || !description.trim()}
          >
            {pending ? "Salvando..." : "Salvar Registro"}
          </button>
          <button type="button" className="records-event-cancel" onClick={onClose}>
            Cancelar
          </button>
        </>
      }
    >
      <form id="add-record-event-form" className="records-event-form" onSubmit={(event) => {
        event.preventDefault();
        onSave({
          type,
          date: date.length === 16 ? `${date}:00` : date,
          description: description.trim(),
          notes: "",
          icd10Code: icd10Code.trim()
        });
      }}>
        <label className="records-event-field records-event-field-full">
          <span className="records-event-label">TIPO DE REGISTRO</span>
          <select value={type} onChange={(event) => setType(event.target.value as EventType)}>
            {eventTypes.map((eventType) => (
              <option key={eventType} value={eventType}>
                {eventLabel[eventType]}
              </option>
            ))}
          </select>
        </label>
        <label className="records-event-field">
          <span className="records-event-label">CID-10 (SE DIAGNÓSTICO)</span>
          <input
            value={icd10Code}
            onChange={(event) => setIcd10Code(event.target.value)}
            placeholder="Ex: I50.0"
          />
        </label>
        <label className="records-event-field">
          <span className="records-event-label">DATA</span>
          <input type="datetime-local" value={date} onChange={(event) => setDate(event.target.value)} required />
        </label>
        <label className="records-event-field records-event-field-full">
          <span className="records-event-label">DESCRIÇÃO / OBSERVAÇÃO</span>
          <textarea
            rows={4}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="Descreva o evento clínico..."
            required
          />
        </label>
      </form>
    </Modal>
  );
}

export default function MedicalRecordsPage() {
  const [params, setParams] = useSearchParams();
  const { notify } = useToast();
  const session = useAuthStore((state) => state.session);
  const canAddEvent =
    session?.role === "ADMIN" ||
    session?.role === "DOCTOR" ||
    session?.role === "NURSE" ||
    session?.role === "RECEPTIONIST";
  const canAddDiagnosis = session?.role === "ADMIN" || session?.role === "DOCTOR";
  const [search, setSearch] = useState("");
  const [showAddModal, setShowAddModal] = useState(false);

  const selectedPatientId = params.get("patientId");
  const { data: patients = [], isLoading: loadingPatients } = usePatients(search);

  const selectedPatientQuery = useQuery({
    queryKey: ["patient", selectedPatientId],
    queryFn: () => getPatientById(selectedPatientId as string),
    enabled: Boolean(selectedPatientId)
  });

  const selectedPatient = selectedPatientQuery.data ?? null;
  const recordQuery = useMedicalRecord(selectedPatientId);
  const addMedicalRecordEvent = useAddMedicalRecordEvent(selectedPatientId, recordQuery.data?.id ?? null);

  const timeline = useMemo(() => {
    const diagnosisEvents: ClinicalEvent[] =
      recordQuery.data?.diagnoses.map((diagnosis) => ({
        id: `${diagnosis.doctorId}-${diagnosis.icd10Code}-${diagnosis.diagnosedAt}`,
        date: diagnosis.diagnosedAt,
        type: "DIAGNOSIS",
        author: diagnosis.doctorId,
        content: `${diagnosis.icd10Code} - ${diagnosis.description}${diagnosis.notes ? ` (${diagnosis.notes})` : ""}`
      })) ?? [];

    const dbEvents: ClinicalEvent[] =
      recordQuery.data?.events.map((event, index) => ({
        id: `${event.type}-${event.occurredAt}-${index}`,
        date: event.occurredAt,
        type: event.type,
        author: event.author,
        content: `${event.description}${event.notes ? ` (${event.notes})` : ""}`
      })) ?? [];

    return [...diagnosisEvents, ...dbEvents].sort(
      (left, right) => new Date(right.date).getTime() - new Date(left.date).getTime()
    );
  }, [recordQuery.data?.diagnoses, recordQuery.data?.events]);

  const openRecord = (patientId: string) => setParams({ patientId });
  const closeRecord = () => setParams({});

  const onSaveEvent = async (payload: {
    type: EventType;
    date: string;
    description: string;
    notes: string;
    icd10Code: string;
  }) => {
    if (!selectedPatientId) {
      return;
    }
    if (!canAddEvent) {
      notify("Seu perfil nao tem permissao para registrar eventos clinicos.", "warning");
      return;
    }

    if (payload.type === "DIAGNOSIS") {
      if (!canAddDiagnosis) {
        notify("Seu perfil não tem permissão para registrar diagnóstico.", "warning");
        return;
      }

      if (!payload.icd10Code) {
        notify("CID-10 é obrigatório para diagnóstico.", "warning");
        return;
      }

      if (!recordQuery.data?.id) {
        notify("Prontuário indisponível para adicionar diagnóstico.", "danger");
        return;
      }

      addMedicalRecordEvent.mutate(
        {
          type: "DIAGNOSIS",
          author: session?.name ?? "Equipe Clinica",
          description: `${payload.icd10Code} - ${payload.description}`,
          notes: payload.notes,
          occurredAt: payload.date
        },
        {
          onSuccess: () => {
            setShowAddModal(false);
            notify("Diagnóstico registrado com sucesso.");
          },
          onError: () => {
            notify("Falha ao registrar diagnóstico.", "danger");
          }
        }
      );
      return;
    }

    addMedicalRecordEvent.mutate(
      {
        type: payload.type,
        author: session?.name ?? "Equipe Clinica",
        description: payload.description,
        notes: payload.notes,
        occurredAt: payload.date
      },
      {
        onSuccess: () => {
          setShowAddModal(false);
          notify("Registro clínico adicionado.");
        },
        onError: () => {
          notify("Falha ao salvar registro clínico.", "danger");
        }
      }
    );
  };

  if (!selectedPatientId) {
    return (
      <div className="stack">
        <PageCard>
          <div className="search-wrap">
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              className="search-input"
              placeholder="Buscar paciente para abrir prontuário"
            />
          </div>
        </PageCard>

        <PageCard title="Selecionar paciente">
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Paciente</th>
                  <th>CPF</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {loadingPatients ? (
                  <tr>
                    <td colSpan={4}>Carregando...</td>
                  </tr>
                ) : (
                  patients.map((patient) => (
                    <tr key={patient.id}>
                      <td>{patient.name}</td>
                      <td>{patient.cpf}</td>
                      <td>{patient.status}</td>
                      <td>
                        <button className="btn-small" type="button" onClick={() => openRecord(patient.id)}>
                          Abrir prontuário
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </PageCard>
      </div>
    );
  }

  return (
    <div className="stack">
      {showAddModal ? (
        <AddEventModal
          onClose={() => setShowAddModal(false)}
          onSave={onSaveEvent}
          pending={addMedicalRecordEvent.isPending}
        />
      ) : null}

      {selectedPatientQuery.isLoading ? <p>Carregando paciente...</p> : null}
      {recordQuery.isLoading ? <p>Carregando prontuário...</p> : null}
      {recordQuery.isError ? <p className="error-text">Não foi possível carregar prontuário.</p> : null}

      {selectedPatient ? (
        <section className="records-workspace">
          <header className="records-toolbar">
            <div className="records-toolbar-left">
              <button className="btn-outline records-back-action" type="button" onClick={closeRecord}>
                <Icon name="arrowLeft" size={15} />
                Voltar
              </button>
              <h2 className="records-toolbar-title">
                Prontuário - {selectedPatient.name}
                <span className="records-patient-tag">{formatPatientTag(selectedPatient.id)}</span>
              </h2>
            </div>
            {canAddEvent ? (
              <button className="btn-outline records-add-action" type="button" onClick={() => setShowAddModal(true)}>
                <Icon name="plus" size={14} />
                Adicionar Registro
              </button>
            ) : null}
          </header>

          <section className="records-grid">
            <aside className="records-aside">
              <article className="records-card">
                <header className="records-card-header">
                  <h3>Dados do Paciente</h3>
                </header>
                <div className="records-card-body">
                  <div className="records-profile">
                    <div className="records-avatar">{getInitials(selectedPatient.name)}</div>
                    <strong>{selectedPatient.name}</strong>
                    <small>{formatCpf(selectedPatient.cpf)}</small>
                  </div>

                  <div className="records-info-list">
                    <div>
                      <span>Idade</span>
                      <strong>{calculateAge(selectedPatient.birthDate)} anos</strong>
                    </div>
                    <div>
                      <span>Tipo Sang.</span>
                      <strong>{selectedPatient.bloodType}</strong>
                    </div>
                    <div>
                      <span>Médico</span>
                      <strong>{timeline[0]?.author ?? "Não informado"}</strong>
                    </div>
                  </div>
                </div>
              </article>

              <article className="records-card">
                <header className="records-card-header">
                  <h3>Legenda</h3>
                </header>
                <div className="records-card-body">
                  <ul className="records-legend">
                    {eventTypes.map((eventType) => (
                      <li key={eventType} className="records-legend-item">
                        <span className={`records-legend-icon ${eventType.toLowerCase()}`}>
                          <Icon name={eventIcon[eventType]} size={14} />
                        </span>
                        <span>{eventLabel[eventType]}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </article>
            </aside>

            <article className="records-card records-timeline-card">
              <header className="records-card-header">
                <h3>Timeline Clínica</h3>
              </header>
              <div className="records-card-body">
                {timeline.length === 0 ? (
                  <div className="empty-state">
                    <p className="empty-title">Prontuário vazio</p>
                    <p className="empty-text">Nenhum evento registrado.</p>
                  </div>
                ) : (
                  <ul className="records-timeline-track">
                    {timeline.map((event) => (
                      <li key={event.id} className="records-timeline-item">
                        <span className={`records-timeline-marker ${event.type.toLowerCase()}`}>
                          <Icon name={eventIcon[event.type]} size={15} />
                        </span>
                        <div className="records-timeline-body">
                          <p className="records-timeline-meta">
                            <span className="records-event-chip">{eventLabel[event.type]}</span>
                            <span>
                              {formatDateTime(event.date)} - {event.author}
                            </span>
                          </p>
                          <p className="records-timeline-content">{event.content}</p>
                        </div>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </article>
          </section>
        </section>
      ) : null}
    </div>
  );
}
