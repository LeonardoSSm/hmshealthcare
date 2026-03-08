package com.medicore.domain.patient;

import com.medicore.domain.shared.BloodType;
import com.medicore.domain.shared.CPF;
import com.medicore.domain.shared.DomainException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Patient {
    private final PatientId id;
    private final CPF cpf;
    private final LocalDateTime createdAt;

    private String name;
    private LocalDate birthDate;
    private BloodType bloodType;
    private String allergies;
    private String phone;
    private String email;
    private String address;
    private PatientStatus status;

    private Patient(
        PatientId id,
        String name,
        CPF cpf,
        LocalDate birthDate,
        BloodType bloodType,
        String allergies,
        String phone,
        String email,
        String address,
        PatientStatus status,
        LocalDateTime createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = validateName(name);
        this.cpf = Objects.requireNonNull(cpf, "cpf cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate cannot be null");
        this.bloodType = Objects.requireNonNull(bloodType, "bloodType cannot be null");
        this.allergies = emptyIfNull(allergies);
        this.phone = emptyIfNull(phone);
        this.email = emptyIfNull(email);
        this.address = emptyIfNull(address);
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public static Patient create(
        String name,
        CPF cpf,
        LocalDate birthDate,
        BloodType bloodType,
        String allergies,
        String phone,
        String email,
        String address
    ) {
        return new Patient(
            PatientId.newId(),
            name,
            cpf,
            birthDate,
            bloodType,
            allergies,
            phone,
            email,
            address,
            PatientStatus.ACTIVE,
            LocalDateTime.now()
        );
    }

    public static Patient rehydrate(
        PatientId id,
        String name,
        CPF cpf,
        LocalDate birthDate,
        BloodType bloodType,
        String allergies,
        String phone,
        String email,
        String address,
        PatientStatus status,
        LocalDateTime createdAt
    ) {
        return new Patient(id, name, cpf, birthDate, bloodType, allergies, phone, email, address, status, createdAt);
    }

    public void updateContact(String phone, String email, String address) {
        this.phone = emptyIfNull(phone);
        this.email = emptyIfNull(email);
        this.address = emptyIfNull(address);
    }

    public void deactivate() {
        this.status = PatientStatus.INACTIVE;
    }

    public void activate() {
        this.status = PatientStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == PatientStatus.ACTIVE;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Patient name cannot be blank");
        }
        return name.trim();
    }

    private static String emptyIfNull(String value) {
        return value == null ? "" : value.trim();
    }

    public PatientId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CPF getCpf() {
        return cpf;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public PatientStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
