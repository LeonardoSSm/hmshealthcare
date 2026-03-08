package com.medicore.application.admission;

import java.util.UUID;

public record AdmitPatientCommand(
    UUID patientId,
    UUID bedId,
    UUID doctorId,
    String reason
) {
}
