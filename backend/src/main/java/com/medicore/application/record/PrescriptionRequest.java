package com.medicore.application.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrescriptionRequest(
    @NotNull UUID doctorId,
    @NotBlank String medication,
    @NotBlank String dosage,
    @NotBlank String frequency,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate
) {
}
