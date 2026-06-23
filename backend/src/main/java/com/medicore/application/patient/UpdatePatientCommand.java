package com.medicore.application.patient;

import java.time.LocalDate;

public record UpdatePatientCommand(
    String name,
    LocalDate birthDate,
    String bloodType,
    String allergies,
    String phone,
    String email,
    String address
) {
}
