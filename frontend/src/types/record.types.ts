export interface DiagnosisEntry {
  doctorId: string;
  icd10Code: string;
  description: string;
  notes: string;
  diagnosedAt: string;
}

export type ClinicalEventType = "ADMISSION" | "DIAGNOSIS" | "PRESCRIPTION" | "OBSERVATION";

export interface MedicalRecordEvent {
  type: ClinicalEventType;
  author: string;
  description: string;
  notes: string;
  occurredAt: string;
}

export interface MedicalRecord {
  id: string;
  patientId: string;
  observations: string;
  diagnoses: DiagnosisEntry[];
  events: MedicalRecordEvent[];
}

export interface CreateDiagnosisPayload {
  doctorId: string;
  icd10Code: string;
  description: string;
  notes: string;
  diagnosedAt: string;
}

export interface CreateMedicalRecordEventPayload {
  type: ClinicalEventType;
  author: string;
  description: string;
  notes: string;
  occurredAt: string;
}
