import { api } from "./api";
import type { Patient } from "../types/patient.types";

export async function listPatients(query: string): Promise<Patient[]> {
  const response = await api.get<Patient[]>("/patients", { params: { query } });
  return response.data;
}
