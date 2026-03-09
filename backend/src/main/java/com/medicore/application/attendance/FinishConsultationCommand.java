package com.medicore.application.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FinishConsultationCommand(
    @NotNull UUID doctorId,
    @NotBlank @Size(max = 2000) String outcome,
    @Size(max = 2000) String notes,
    @Size(max = 120) String requestedBy
) {
}
