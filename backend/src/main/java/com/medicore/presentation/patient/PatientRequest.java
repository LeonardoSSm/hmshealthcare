package com.medicore.presentation.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PatientRequest(
    @NotBlank String name,
    @NotBlank String cpf,
    @NotNull @Past LocalDate birthDate,
    @NotBlank String bloodType,
    String allergies,
    String phone,
    @Email String email,
    String address
) {
}
