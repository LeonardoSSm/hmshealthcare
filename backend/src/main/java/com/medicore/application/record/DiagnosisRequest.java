package com.medicore.application.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record DiagnosisRequest(
    @NotNull UUID doctorId,
    @NotBlank @Size(max = 16) String icd10Code,
    @NotBlank @Size(max = 255) String description,
    String notes,
    LocalDateTime diagnosedAt
) {
}
