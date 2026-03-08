package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdmissionResponse(
    UUID id,
    UUID patientId,
    UUID bedId,
    UUID doctorId,
    LocalDateTime admissionDate,
    LocalDateTime dischargeDate,
    String reason,
    String status
) {
    public static AdmissionResponse from(Admission admission) {
        return new AdmissionResponse(
            admission.getId(),
            admission.getPatientId().value(),
            admission.getBedId(),
            admission.getDoctorId(),
            admission.getAdmissionDate(),
            admission.getDischargeDate(),
            admission.getReason(),
            admission.getStatus().name()
        );
    }
}
