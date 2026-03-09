package com.medicore.application.attendance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CheckInAttendanceCommand(
    @NotNull UUID patientId,
    @Size(max = 500) String notes,
    @Size(max = 120) String requestedBy
) {
}
