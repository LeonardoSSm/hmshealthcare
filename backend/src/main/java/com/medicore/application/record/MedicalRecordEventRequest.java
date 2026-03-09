package com.medicore.application.record;

import com.medicore.domain.record.ClinicalEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MedicalRecordEventRequest(
    @NotNull ClinicalEventType type,
    @NotBlank @Size(max = 120) String author,
    @NotBlank @Size(max = 1000) String description,
    String notes,
    LocalDateTime occurredAt
) {
}
