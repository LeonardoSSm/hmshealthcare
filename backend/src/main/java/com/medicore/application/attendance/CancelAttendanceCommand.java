package com.medicore.application.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelAttendanceCommand(
    @NotBlank @Size(max = 500) String reason,
    @Size(max = 120) String requestedBy
) {
}
