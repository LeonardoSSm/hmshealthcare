package com.medicore.application.attendance;

import com.medicore.domain.attendance.RiskLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record FinishTriageCommand(
    @NotNull UUID nurseId,
    @NotNull RiskLevel riskLevel,
    @Size(max = 2000) String notes,
    @Size(max = 120) String requestedBy
) {
}
