package com.medicore.application.patient;

import com.medicore.domain.patient.Patient;

public final class PatientResponseMapper {
    private PatientResponseMapper() {
    }

    public static PatientResponse toResponse(Patient patient) {
        return new PatientResponse(
            patient.getId().value(),
            patient.getName(),
            patient.getCpf().value(),
            patient.getBirthDate(),
            patient.getBloodType().name(),
            patient.getAllergies(),
            patient.getPhone(),
            patient.getEmail(),
            patient.getAddress(),
            patient.getStatus().name(),
            patient.getCreatedAt()
        );
    }
}
