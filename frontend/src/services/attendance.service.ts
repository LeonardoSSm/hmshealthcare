import { api } from "./api";
import type { Attendance, QueuePanelItem, RiskLevel } from "../types/attendance.types";

interface BaseActionPayload {
  requestedBy?: string;
}

export interface CheckInPayload extends BaseActionPayload {
  patientId: string;
  notes?: string;
}

export interface StartTriagePayload extends BaseActionPayload {
  nurseId: string;
}

export interface FinishTriagePayload extends BaseActionPayload {
  nurseId: string;
  riskLevel: RiskLevel;
  notes?: string;
}

export interface CallDoctorPayload extends BaseActionPayload {
  doctorId: string;
  roomLabel: string;
}

export interface StartConsultationPayload extends BaseActionPayload {
  doctorId: string;
}

export interface FinishConsultationPayload extends BaseActionPayload {
  doctorId: string;
  outcome: string;
  notes?: string;
}

export interface CancelAttendancePayload extends BaseActionPayload {
  reason: string;
}

export async function listAttendances(includeClosed = false): Promise<Attendance[]> {
  const response = await api.get<Attendance[]>("/attendances", { params: { includeClosed } });
  return response.data;
}

export async function checkInAttendance(payload: CheckInPayload): Promise<Attendance> {
  const response = await api.post<Attendance>("/attendances/check-in", payload);
  return response.data;
}

export async function startTriage(attendanceId: string, payload: StartTriagePayload): Promise<Attendance> {
  const response = await api.post<Attendance>(`/attendances/${attendanceId}/start-triage`, payload);
  return response.data;
}

export async function finishTriage(attendanceId: string, payload: FinishTriagePayload): Promise<Attendance> {
  const response = await api.post<Attendance>(`/attendances/${attendanceId}/finish-triage`, payload);
  return response.data;
}

export async function callDoctor(attendanceId: string, payload: CallDoctorPayload): Promise<Attendance> {
  const response = await api.post<Attendance>(`/attendances/${attendanceId}/call-doctor`, payload);
  return response.data;
}

export async function startConsultation(attendanceId: string, payload: StartConsultationPayload): Promise<Attendance> {
  const response = await api.post<Attendance>(`/attendances/${attendanceId}/start-consultation`, payload);
  return response.data;
}

export async function finishConsultation(
  attendanceId: string,
  payload: FinishConsultationPayload
): Promise<Attendance> {
  const response = await api.post<Attendance>(`/attendances/${attendanceId}/finish-consultation`, payload);
  return response.data;
}

export async function cancelAttendance(attendanceId: string, payload: CancelAttendancePayload): Promise<Attendance> {
  const response = await api.post<Attendance>(`/attendances/${attendanceId}/cancel`, payload);
  return response.data;
}

export async function listQueuePanel(): Promise<QueuePanelItem[]> {
  const response = await api.get<QueuePanelItem[]>("/panel/queue");
  return response.data;
}
