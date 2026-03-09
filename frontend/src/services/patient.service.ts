import { api } from "./api";
import type { CreatePatientPayload, Patient } from "../types/patient.types";

const uiToApiBloodType: Record<string, string> = {
  "A+": "A_POS",
  "A-": "A_NEG",
  "B+": "B_POS",
  "B-": "B_NEG",
  "AB+": "AB_POS",
  "AB-": "AB_NEG",
  "O+": "O_POS",
  "O-": "O_NEG"
};

const apiToUiBloodType: Record<string, string> = {
  A_POS: "A+",
  A_NEG: "A-",
  B_POS: "B+",
  B_NEG: "B-",
  AB_POS: "AB+",
  AB_NEG: "AB-",
  O_POS: "O+",
  O_NEG: "O-"
};

function toUiPatient(patient: Patient): Patient {
  return {
    ...patient,
    bloodType: apiToUiBloodType[patient.bloodType] ?? patient.bloodType
  };
}

export async function listPatients(query: string): Promise<Patient[]> {
  const response = await api.get<Patient[]>("/patients", { params: { query } });
  return response.data.map(toUiPatient);
}

export async function getPatientById(id: string): Promise<Patient> {
  const response = await api.get<Patient>(`/patients/${id}`);
  return toUiPatient(response.data);
}

export async function createPatient(payload: CreatePatientPayload): Promise<Patient> {
  const response = await api.post<Patient>("/patients", {
    ...payload,
    bloodType: uiToApiBloodType[payload.bloodType] ?? payload.bloodType
  });
  return toUiPatient(response.data);
}

export async function deactivatePatient(id: string): Promise<void> {
  await api.delete(`/patients/${id}`);
}
