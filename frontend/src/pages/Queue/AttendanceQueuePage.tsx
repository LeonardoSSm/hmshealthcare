import { useMemo, useState } from "react";
import { usePatients } from "../../hooks/usePatients";
import { useDoctors, useNurses } from "../../hooks/useUsers";
import {
  useAttendancesQueue,
  useCallDoctor,
  useCancelAttendance,
  useCheckInAttendance,
  useFinishConsultation,
  useFinishTriage,
  useStartConsultation,
  useStartTriage
} from "../../hooks/useAttendancesQueue";
import { PageCard } from "../../components/ui/PageCard";
import { Modal } from "../../components/ui/Modal";
import { StatusBadge } from "../../components/ui/StatusBadge";
import { Icon } from "../../components/ui/Icon";
import { useToast } from "../../contexts/ToastContext";
import { useAuthStore } from "../../store/authStore";
import { extractApiErrorMessage } from "../../lib/apiError";
import { formatDateTime } from "../../lib/format";
import type { Attendance, RiskLevel } from "../../types/attendance.types";

const riskOptions: RiskLevel[] = ["RED", "ORANGE", "YELLOW", "GREEN", "BLUE"];

const riskLabel: Record<RiskLevel, string> = {
  RED: "Vermelho",
  ORANGE: "Laranja",
  YELLOW: "Amarelo",
  GREEN: "Verde",
  BLUE: "Azul"
};

const statusLabel: Record<string, string> = {
  WAITING_TRIAGE: "Aguardando triagem",
  IN_TRIAGE: "Em triagem",
  WAITING_DOCTOR: "Aguardando medico",
  CALLED_DOCTOR: "Chamado ao consultorio",
  IN_CONSULTATION: "Em consulta",
  COMPLETED: "Concluido",
  CANCELLED: "Cancelado"
};

const statusTone: Record<string, "warning" | "info" | "success" | "danger" | "neutral"> = {
  WAITING_TRIAGE: "warning",
  IN_TRIAGE: "info",
  WAITING_DOCTOR: "warning",
  CALLED_DOCTOR: "info",
  IN_CONSULTATION: "info",
  COMPLETED: "success",
  CANCELLED: "danger"
};

function getWaitingMinutes(startAt: string): number {
  const start = new Date(startAt).getTime();
  if (!Number.isFinite(start)) {
    return 0;
  }
  return Math.max(0, Math.floor((Date.now() - start) / 60000));
}

export default function AttendanceQueuePage() {
  const { notify } = useToast();
  const session = useAuthStore((state) => state.session);
  const [search, setSearch] = useState("");
  const [showCheckInModal, setShowCheckInModal] = useState(false);
  const [showTriageModal, setShowTriageModal] = useState<Attendance | null>(null);
  const [showCallDoctorModal, setShowCallDoctorModal] = useState<Attendance | null>(null);
  const [showFinishConsultationModal, setShowFinishConsultationModal] = useState<Attendance | null>(null);

  const canCheckIn = session?.role === "ADMIN" || session?.role === "RECEPTIONIST";
  const canTriage = session?.role === "ADMIN" || session?.role === "NURSE";
  const canCallDoctor = session?.role === "ADMIN" || session?.role === "NURSE" || session?.role === "RECEPTIONIST";
  const canConsult = session?.role === "ADMIN" || session?.role === "DOCTOR";

  const queueQuery = useAttendancesQueue(false);
  const { data: patients = [] } = usePatients("");
  const { data: nurses = [] } = useNurses();
  const { data: doctors = [] } = useDoctors();

  const checkInMutation = useCheckInAttendance();
  const startTriageMutation = useStartTriage();
  const finishTriageMutation = useFinishTriage();
  const callDoctorMutation = useCallDoctor();
  const startConsultationMutation = useStartConsultation();
  const finishConsultationMutation = useFinishConsultation();
  const cancelMutation = useCancelAttendance();

  const filteredAttendances = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) {
      return queueQuery.data ?? [];
    }
    return (queueQuery.data ?? []).filter((attendance) => {
      return (
        attendance.ticketNumber.toLowerCase().includes(term) ||
        attendance.patientName.toLowerCase().includes(term) ||
        (statusLabel[attendance.status] ?? attendance.status).toLowerCase().includes(term) ||
        (attendance.roomLabel ?? "").toLowerCase().includes(term)
      );
    });
  }, [queueQuery.data, search]);

  return (
    <div className="stack">
      {showCheckInModal ? (
        <CheckInModal
          patients={patients.filter((patient) => patient.status === "ACTIVE")}
          pending={checkInMutation.isPending}
          onClose={() => setShowCheckInModal(false)}
          onSubmit={(payload) =>
            checkInMutation.mutate(
              { ...payload, requestedBy: session?.name ?? "Recepcao" },
              {
                onSuccess: () => {
                  setShowCheckInModal(false);
                  notify("Paciente entrou na fila com sucesso.");
                },
                onError: (error) => notify(extractApiErrorMessage(error, "Falha ao registrar entrada."), "danger")
              }
            )
          }
        />
      ) : null}

      {showTriageModal ? (
        <FinishTriageModal
          attendance={showTriageModal}
          nurses={nurses}
          pending={finishTriageMutation.isPending}
          onClose={() => setShowTriageModal(null)}
          onSubmit={(payload) =>
            finishTriageMutation.mutate(
              {
                attendanceId: showTriageModal.id,
                payload: { ...payload, requestedBy: session?.name ?? "Enfermagem" }
              },
              {
                onSuccess: () => {
                  setShowTriageModal(null);
                  notify("Classificacao de risco registrada.");
                },
                onError: (error) => notify(extractApiErrorMessage(error, "Falha ao finalizar triagem."), "danger")
              }
            )
          }
        />
      ) : null}

      {showCallDoctorModal ? (
        <CallDoctorModal
          doctors={doctors}
          pending={callDoctorMutation.isPending}
          onClose={() => setShowCallDoctorModal(null)}
          onSubmit={(payload) =>
            callDoctorMutation.mutate(
              {
                attendanceId: showCallDoctorModal.id,
                payload: { ...payload, requestedBy: session?.name ?? "Recepcao" }
              },
              {
                onSuccess: () => {
                  setShowCallDoctorModal(null);
                  notify("Paciente chamado para consultorio.");
                },
                onError: (error) => notify(extractApiErrorMessage(error, "Falha ao chamar medico."), "danger")
              }
            )
          }
        />
      ) : null}

      {showFinishConsultationModal ? (
        <FinishConsultationModal
          doctors={doctors}
          attendance={showFinishConsultationModal}
          pending={finishConsultationMutation.isPending}
          onClose={() => setShowFinishConsultationModal(null)}
          onSubmit={(payload) =>
            finishConsultationMutation.mutate(
              {
                attendanceId: showFinishConsultationModal.id,
                payload: { ...payload, requestedBy: session?.name ?? "Medico" }
              },
              {
                onSuccess: () => {
                  setShowFinishConsultationModal(null);
                  notify("Atendimento finalizado.");
                },
                onError: (error) =>
                  notify(extractApiErrorMessage(error, "Falha ao finalizar atendimento."), "danger")
              }
            )
          }
        />
      ) : null}

      <PageCard
        title="Fila de atendimento"
        actions={
          <div className="inline-actions">
            <button
              type="button"
              className="btn-outline"
              onClick={() => window.open("/queue/panel", "_blank", "noopener,noreferrer")}
            >
              <Icon name="display" size={14} /> Abrir painel TV
            </button>
            <button
              type="button"
              className="btn-small"
              disabled={!canCheckIn}
              onClick={() => {
                if (!canCheckIn) {
                  notify("Seu perfil nao pode registrar entrada na fila.", "warning");
                  return;
                }
                setShowCheckInModal(true);
              }}
            >
              + Dar entrada
            </button>
          </div>
        }
      >
        <div className="search-wrap">
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar por senha, paciente, status ou sala"
            className="search-input"
          />
        </div>
      </PageCard>

      <PageCard title="Fluxo atual">
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Senha</th>
                <th>Paciente</th>
                <th>Status</th>
                <th>Risco</th>
                <th>Sala</th>
                <th>Entrada</th>
                <th>Espera</th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              {queueQuery.isLoading ? (
                <tr>
                  <td colSpan={8}>Carregando fila...</td>
                </tr>
              ) : null}
              {queueQuery.isError ? (
                <tr>
                  <td colSpan={8}>Nao foi possivel carregar a fila.</td>
                </tr>
              ) : null}
              {!queueQuery.isLoading && !queueQuery.isError && filteredAttendances.length === 0 ? (
                <tr>
                  <td colSpan={8}>Nenhum atendimento em aberto.</td>
                </tr>
              ) : null}
              {filteredAttendances.map((attendance) => (
                <tr key={attendance.id}>
                  <td>{attendance.ticketNumber}</td>
                  <td>{attendance.patientName}</td>
                  <td>
                    <StatusBadge
                      tone={statusTone[attendance.status] ?? "neutral"}
                      label={statusLabel[attendance.status] ?? attendance.status}
                    />
                  </td>
                  <td>{attendance.riskLevel ? riskLabel[attendance.riskLevel] : "-"}</td>
                  <td>{attendance.roomLabel ?? "-"}</td>
                  <td>{formatDateTime(attendance.checkInAt)}</td>
                  <td>{getWaitingMinutes(attendance.checkInAt)} min</td>
                  <td>
                    <div className="inline-actions">
                      {attendance.status === "WAITING_TRIAGE" ? (
                        <button
                          type="button"
                          className="btn-outline"
                          disabled={!canTriage || startTriageMutation.isPending || nurses.length === 0}
                          onClick={() => {
                            if (!canTriage) {
                              notify("Seu perfil nao pode iniciar triagem.", "warning");
                              return;
                            }
                            const preferredNurseId =
                              session?.role === "NURSE"
                                ? nurses.find((item) => item.email === session.email)?.id ?? nurses[0]?.id
                                : nurses[0]?.id;
                            if (!preferredNurseId) {
                              notify("Nao ha enfermeiros ativos cadastrados.", "warning");
                              return;
                            }
                            startTriageMutation.mutate(
                              {
                                attendanceId: attendance.id,
                                payload: { nurseId: preferredNurseId, requestedBy: session?.name ?? "Enfermagem" }
                              },
                              {
                                onSuccess: () => notify("Triagem iniciada."),
                                onError: (error) =>
                                  notify(extractApiErrorMessage(error, "Falha ao iniciar triagem."), "danger")
                              }
                            );
                          }}
                        >
                          Iniciar triagem
                        </button>
                      ) : null}

                      {attendance.status === "IN_TRIAGE" ? (
                        <button
                          type="button"
                          className="btn-small"
                          disabled={!canTriage}
                          onClick={() => {
                            if (!canTriage) {
                              notify("Seu perfil nao pode finalizar triagem.", "warning");
                              return;
                            }
                            setShowTriageModal(attendance);
                          }}
                        >
                          Classificar risco
                        </button>
                      ) : null}

                      {attendance.status === "WAITING_DOCTOR" ? (
                        <button
                          type="button"
                          className="btn-small"
                          disabled={!canCallDoctor}
                          onClick={() => {
                            if (!canCallDoctor) {
                              notify("Seu perfil nao pode chamar para consultorio.", "warning");
                              return;
                            }
                            setShowCallDoctorModal(attendance);
                          }}
                        >
                          Chamar medico
                        </button>
                      ) : null}

                      {attendance.status === "CALLED_DOCTOR" ? (
                        <button
                          type="button"
                          className="btn-outline"
                          disabled={!canConsult || startConsultationMutation.isPending}
                          onClick={() => {
                            if (!canConsult) {
                              notify("Seu perfil nao pode iniciar consulta.", "warning");
                              return;
                            }
                            const preferredDoctorId =
                              session?.role === "DOCTOR"
                                ? doctors.find((item) => item.email === session.email)?.id ?? attendance.doctorId
                                : attendance.doctorId;
                            if (!preferredDoctorId) {
                              notify("Selecione um medico antes de iniciar consulta.", "warning");
                              return;
                            }
                            startConsultationMutation.mutate(
                              {
                                attendanceId: attendance.id,
                                payload: { doctorId: preferredDoctorId, requestedBy: session?.name ?? "Medico" }
                              },
                              {
                                onSuccess: () => notify("Consulta iniciada."),
                                onError: (error) =>
                                  notify(extractApiErrorMessage(error, "Falha ao iniciar consulta."), "danger")
                              }
                            );
                          }}
                        >
                          Iniciar consulta
                        </button>
                      ) : null}

                      {attendance.status === "IN_CONSULTATION" ? (
                        <button
                          type="button"
                          className="btn-small"
                          disabled={!canConsult}
                          onClick={() => {
                            if (!canConsult) {
                              notify("Seu perfil nao pode finalizar consulta.", "warning");
                              return;
                            }
                            setShowFinishConsultationModal(attendance);
                          }}
                        >
                          Finalizar
                        </button>
                      ) : null}

                      {attendance.status !== "COMPLETED" && attendance.status !== "CANCELLED" ? (
                        <button
                          type="button"
                          className="btn-outline"
                          disabled={!canCheckIn || cancelMutation.isPending}
                          onClick={() =>
                            cancelMutation.mutate(
                              {
                                attendanceId: attendance.id,
                                payload: { reason: "Cancelado pela recepcao", requestedBy: session?.name ?? "Recepcao" }
                              },
                              {
                                onSuccess: () => notify("Atendimento cancelado."),
                                onError: (error) =>
                                  notify(extractApiErrorMessage(error, "Falha ao cancelar atendimento."), "danger")
                              }
                            )
                          }
                        >
                          Cancelar
                        </button>
                      ) : null}
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

function CheckInModal({
  patients,
  pending,
  onClose,
  onSubmit
}: {
  patients: Array<{ id: string; name: string }>;
  pending: boolean;
  onClose: () => void;
  onSubmit: (payload: { patientId: string; notes?: string }) => void;
}) {
  const [patientId, setPatientId] = useState("");
  const [notes, setNotes] = useState("");

  return (
    <Modal
      title="Dar entrada na fila"
      subtitle="Paciente aguardara chamada para triagem"
      onClose={onClose}
      footer={
        <>
          <button
            type="submit"
            form="check-in-form"
            className="btn-small"
            disabled={pending || !patientId}
          >
            {pending ? "Salvando..." : "Confirmar entrada"}
          </button>
          <button type="button" className="btn-outline" onClick={onClose}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="check-in-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit({ patientId, notes: notes.trim() || undefined });
        }}
      >
        <label className="full-width">
          Paciente
          <select value={patientId} onChange={(event) => setPatientId(event.target.value)} required>
            <option value="">Selecione</option>
            {patients.map((patient) => (
              <option key={patient.id} value={patient.id}>
                {patient.name}
              </option>
            ))}
          </select>
        </label>
        <label className="full-width">
          Queixa inicial
          <textarea rows={3} value={notes} onChange={(event) => setNotes(event.target.value)} />
        </label>
      </form>
    </Modal>
  );
}

function FinishTriageModal({
  attendance,
  nurses,
  pending,
  onClose,
  onSubmit
}: {
  attendance: Attendance;
  nurses: Array<{ id: string; name: string }>;
  pending: boolean;
  onClose: () => void;
  onSubmit: (payload: { nurseId: string; riskLevel: RiskLevel; notes?: string }) => void;
}) {
  const [nurseId, setNurseId] = useState(attendance.nurseId ?? nurses[0]?.id ?? "");
  const [riskLevel, setRiskLevel] = useState<RiskLevel>("YELLOW");
  const [notes, setNotes] = useState("");

  return (
    <Modal
      title={`Classificacao de risco - ${attendance.ticketNumber}`}
      subtitle={attendance.patientName}
      onClose={onClose}
      footer={
        <>
          <button type="submit" form="finish-triage-form" className="btn-small" disabled={pending || !nurseId}>
            {pending ? "Salvando..." : "Salvar classificacao"}
          </button>
          <button type="button" className="btn-outline" onClick={onClose}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="finish-triage-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit({ nurseId, riskLevel, notes: notes.trim() || undefined });
        }}
      >
        <label>
          Enfermeiro responsavel
          <select value={nurseId} onChange={(event) => setNurseId(event.target.value)} required>
            <option value="">Selecione</option>
            {nurses.map((nurse) => (
              <option key={nurse.id} value={nurse.id}>
                {nurse.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Nivel de risco
          <select value={riskLevel} onChange={(event) => setRiskLevel(event.target.value as RiskLevel)} required>
            {riskOptions.map((option) => (
              <option key={option} value={option}>
                {riskLabel[option]}
              </option>
            ))}
          </select>
        </label>
        <label className="full-width">
          Observacoes
          <textarea rows={3} value={notes} onChange={(event) => setNotes(event.target.value)} />
        </label>
      </form>
    </Modal>
  );
}

function CallDoctorModal({
  doctors,
  pending,
  onClose,
  onSubmit
}: {
  doctors: Array<{ id: string; name: string }>;
  pending: boolean;
  onClose: () => void;
  onSubmit: (payload: { doctorId: string; roomLabel: string }) => void;
}) {
  const [doctorId, setDoctorId] = useState(doctors[0]?.id ?? "");
  const [roomLabel, setRoomLabel] = useState("");

  return (
    <Modal
      title="Chamar para consultorio"
      subtitle="Defina medico e sala"
      onClose={onClose}
      footer={
        <>
          <button
            type="submit"
            form="call-doctor-form"
            className="btn-small"
            disabled={pending || !doctorId || !roomLabel.trim()}
          >
            {pending ? "Salvando..." : "Confirmar chamada"}
          </button>
          <button type="button" className="btn-outline" onClick={onClose}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="call-doctor-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit({ doctorId, roomLabel: roomLabel.trim() });
        }}
      >
        <label>
          Medico
          <select value={doctorId} onChange={(event) => setDoctorId(event.target.value)} required>
            <option value="">Selecione</option>
            {doctors.map((doctor) => (
              <option key={doctor.id} value={doctor.id}>
                {doctor.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Consultorio
          <input value={roomLabel} onChange={(event) => setRoomLabel(event.target.value)} placeholder="Ex: C-03" required />
        </label>
      </form>
    </Modal>
  );
}

function FinishConsultationModal({
  attendance,
  doctors,
  pending,
  onClose,
  onSubmit
}: {
  attendance: Attendance;
  doctors: Array<{ id: string; name: string }>;
  pending: boolean;
  onClose: () => void;
  onSubmit: (payload: { doctorId: string; outcome: string; notes?: string }) => void;
}) {
  const [doctorId, setDoctorId] = useState(attendance.doctorId ?? doctors[0]?.id ?? "");
  const [outcome, setOutcome] = useState("");
  const [notes, setNotes] = useState("");

  return (
    <Modal
      title={`Finalizar atendimento - ${attendance.ticketNumber}`}
      subtitle={attendance.patientName}
      onClose={onClose}
      footer={
        <>
          <button
            type="submit"
            form="finish-consultation-form"
            className="btn-small"
            disabled={pending || !doctorId || !outcome.trim()}
          >
            {pending ? "Salvando..." : "Finalizar"}
          </button>
          <button type="button" className="btn-outline" onClick={onClose}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="finish-consultation-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit({ doctorId, outcome: outcome.trim(), notes: notes.trim() || undefined });
        }}
      >
        <label>
          Medico
          <select value={doctorId} onChange={(event) => setDoctorId(event.target.value)} required>
            <option value="">Selecione</option>
            {doctors.map((doctor) => (
              <option key={doctor.id} value={doctor.id}>
                {doctor.name}
              </option>
            ))}
          </select>
        </label>
        <label className="full-width">
          Conduta / orientacoes
          <textarea rows={3} value={outcome} onChange={(event) => setOutcome(event.target.value)} required />
        </label>
        <label className="full-width">
          Observacoes complementares
          <textarea rows={3} value={notes} onChange={(event) => setNotes(event.target.value)} />
        </label>
      </form>
    </Modal>
  );
}
