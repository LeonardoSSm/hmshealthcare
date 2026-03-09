import { api } from "./api";
import type {
  CreateDiagnosisPayload,
  CreateMedicalRecordEventPayload,
  MedicalRecord
} from "../types/record.types";

export async function getMedicalRecordByPatient(patientId: string): Promise<MedicalRecord> {
  const response = await api.get<MedicalRecord>(`/medical-records/patient/${patientId}`);
  return response.data;
}

export async function addDiagnosis(
  medicalRecordId: string,
  payload: CreateDiagnosisPayload
): Promise<MedicalRecord> {
  const response = await api.post<MedicalRecord>(
    `/medical-records/${medicalRecordId}/diagnoses`,
    payload
  );
  return response.data;
}

export async function addMedicalRecordEvent(
  medicalRecordId: string,
  payload: CreateMedicalRecordEventPayload
): Promise<MedicalRecord> {
  const response = await api.post<MedicalRecord>(`/medical-records/${medicalRecordId}/events`, payload);
  return response.data;
}
