import { api } from "./api";

export interface AdmissionRemoteResponse {
  id: string;
  patientId: string;
  bedId: string;
  doctorId: string;
  admissionDate: string;
  dischargeDate: string | null;
  reason: string;
  status: "ACTIVE" | "DISCHARGED";
}

export interface AdmitRemotePayload {
  patientId: string;
  bedId: string;
  doctorId: string;
  reason: string;
}

export async function listAdmissions(): Promise<AdmissionRemoteResponse[]> {
  const response = await api.get<AdmissionRemoteResponse[]>("/admissions");
  return response.data;
}

export async function listAdmissionsByPatient(patientId: string): Promise<AdmissionRemoteResponse[]> {
  const response = await api.get<AdmissionRemoteResponse[]>(`/admissions/patient/${patientId}`);
  return response.data;
}

export async function admitPatientRemote(payload: AdmitRemotePayload): Promise<AdmissionRemoteResponse> {
  const response = await api.post<AdmissionRemoteResponse>("/admissions", payload);
  return response.data;
}

export async function dischargeAdmissionRemote(admissionId: string): Promise<AdmissionRemoteResponse> {
  const response = await api.patch<AdmissionRemoteResponse>(`/admissions/${admissionId}/discharge`);
  return response.data;
}
