package com.medicore.application.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientResponse(
    UUID id,
    String name,
    String cpf,
    LocalDate birthDate,
    String bloodType,
    String allergies,
    String phone,
    String email,
    String address,
    String status,
    LocalDateTime createdAt
) {
}
