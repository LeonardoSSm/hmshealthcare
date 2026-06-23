package com.medicore.application.record;

import com.medicore.domain.record.Diagnosis;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiagnosisResponse(
    UUID doctorId,
    String icd10Code,
    String description,
    String notes,
    LocalDateTime diagnosedAt
) {
    public static DiagnosisResponse from(Diagnosis d) {
        return new DiagnosisResponse(d.doctorId(), d.icd10Code(), d.description(), d.notes(), d.diagnosedAt());
    }
}
