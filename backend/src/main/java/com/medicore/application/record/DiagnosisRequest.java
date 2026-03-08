package com.medicore.application.record;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiagnosisRequest(
    UUID doctorId,
    String icd10Code,
    String description,
    String notes,
    LocalDateTime diagnosedAt
) {
}
