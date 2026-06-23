package com.medicore.application.record;

import com.medicore.domain.record.MedicalRecord;

import java.util.List;
import java.util.UUID;

public record MedicalRecordResponse(
    UUID id,
    UUID patientId,
    String observations,
    List<DiagnosisResponse> diagnoses,
    List<PrescriptionResponse> prescriptions,
    List<MedicalRecordEventResponse> events
) {
    public static MedicalRecordResponse from(MedicalRecord medicalRecord) {
        return new MedicalRecordResponse(
            medicalRecord.getId(),
            medicalRecord.getPatientId().value(),
            medicalRecord.getObservations(),
            medicalRecord.getDiagnoses().stream().map(DiagnosisResponse::from).toList(),
            medicalRecord.getPrescriptions().stream().map(PrescriptionResponse::from).toList(),
            medicalRecord.getEvents().stream().map(MedicalRecordEventResponse::from).toList()
        );
    }
}
