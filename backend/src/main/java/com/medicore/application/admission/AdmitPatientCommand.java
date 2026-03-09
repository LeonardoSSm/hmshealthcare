package com.medicore.application.admission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AdmitPatientCommand(
    @NotNull UUID patientId,
    @NotNull UUID bedId,
    @NotNull UUID doctorId,
    @NotBlank @Size(max = 255) String reason
) {
}
