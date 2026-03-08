package com.medicore.infrastructure.persistence.record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordJpaRepository extends JpaRepository<MedicalRecordEntity, String> {
    Optional<MedicalRecordEntity> findByPatientId(String patientId);
}
