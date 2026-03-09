package com.medicore.application.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CallDoctorCommand(
    @NotNull UUID doctorId,
    @NotBlank @Size(max = 40) String roomLabel,
    @Size(max = 120) String requestedBy
) {
}
