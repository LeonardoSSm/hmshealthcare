package com.medicore.application.attendance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record StartTriageCommand(
    @NotNull UUID nurseId,
    @Size(max = 120) String requestedBy
) {
}
