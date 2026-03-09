export type AttendanceStatus =
  | "WAITING_TRIAGE"
  | "IN_TRIAGE"
  | "WAITING_DOCTOR"
  | "CALLED_DOCTOR"
  | "IN_CONSULTATION"
  | "COMPLETED"
  | "CANCELLED";

export type RiskLevel = "RED" | "ORANGE" | "YELLOW" | "GREEN" | "BLUE";

export interface Attendance {
  id: string;
  ticketNumber: string;
  patientId: string;
  patientName: string;
  status: AttendanceStatus;
  riskLevel: RiskLevel | null;
  priorityScore: number;
  nurseId: string | null;
  doctorId: string | null;
  roomLabel: string | null;
  checkInNotes: string | null;
  triageNotes: string | null;
  consultationNotes: string | null;
  outcome: string | null;
  checkInAt: string;
  triageStartedAt: string | null;
  triageFinishedAt: string | null;
  calledAt: string | null;
  consultationStartedAt: string | null;
  consultationFinishedAt: string | null;
  updatedAt: string;
}

export interface QueuePanelItem {
  attendanceId: string;
  ticketNumber: string;
  displayName: string;
  status: AttendanceStatus;
  riskLevel: RiskLevel | null;
  roomLabel: string | null;
  calledAt: string | null;
  checkInAt: string;
}
