package com.medicore.application.record;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.record.MedicalRecord;

import java.util.List;
import java.util.UUID;

public record MedicalRecordResponse(
    UUID id,
    UUID patientId,
    String observations,
    List<DiagnosisRequest> diagnoses
) {
    public static MedicalRecordResponse from(MedicalRecord medicalRecord) {
        return new MedicalRecordResponse(
            medicalRecord.getId(),
            medicalRecord.getPatientId().value(),
            medicalRecord.getObservations(),
            medicalRecord.getDiagnoses().stream()
                .map(d -> new DiagnosisRequest(d.doctorId(), d.icd10Code(), d.description(), d.notes(), d.diagnosedAt()))
                .toList()
        );
    }
}
