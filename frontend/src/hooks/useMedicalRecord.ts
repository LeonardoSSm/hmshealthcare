import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  addDiagnosis,
  addMedicalRecordEvent,
  getMedicalRecordByPatient
} from "../services/medicalRecord.service";
import type { CreateDiagnosisPayload, CreateMedicalRecordEventPayload } from "../types/record.types";

export function useMedicalRecord(patientId: string | null) {
  return useQuery({
    queryKey: ["medical-record", patientId],
    queryFn: () => getMedicalRecordByPatient(patientId as string),
    enabled: Boolean(patientId)
  });
}

export function useAddDiagnosis(patientId: string | null, medicalRecordId: string | null) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateDiagnosisPayload) => addDiagnosis(medicalRecordId as string, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["medical-record", patientId] });
    }
  });
}

export function useAddMedicalRecordEvent(patientId: string | null, medicalRecordId: string | null) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateMedicalRecordEventPayload) =>
      addMedicalRecordEvent(medicalRecordId as string, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["medical-record", patientId] });
    }
  });
}
