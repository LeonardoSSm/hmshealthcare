package com.medicore.infrastructure.persistence.patient;

import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientStatus;
import com.medicore.domain.shared.BloodType;
import com.medicore.domain.shared.CPF;

import java.util.UUID;

public final class PatientMapper {
    private PatientMapper() {
    }

    public static PatientEntity toEntity(Patient patient) {
        PatientEntity entity = new PatientEntity();
        entity.setId(patient.getId().value().toString());
        entity.setName(patient.getName());
        entity.setCpf(patient.getCpf().value());
        entity.setBirthDate(patient.getBirthDate());
        entity.setBloodType(patient.getBloodType().name());
        entity.setAllergies(patient.getAllergies());
        entity.setPhone(patient.getPhone());
        entity.setEmail(patient.getEmail());
        entity.setAddress(patient.getAddress());
        entity.setStatus(patient.getStatus().name());
        entity.setCreatedAt(patient.getCreatedAt());
        return entity;
    }

    public static Patient toDomain(PatientEntity entity) {
        return Patient.rehydrate(
            new PatientId(UUID.fromString(entity.getId())),
            entity.getName(),
            new CPF(entity.getCpf()),
            entity.getBirthDate(),
            BloodType.valueOf(entity.getBloodType()),
            entity.getAllergies(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getAddress(),
            PatientStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt()
        );
    }
}
