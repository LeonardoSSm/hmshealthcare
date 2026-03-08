package com.medicore.infrastructure.persistence.admission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdmissionJpaRepository extends JpaRepository<AdmissionEntity, String> {
    List<AdmissionEntity> findByPatientId(String patientId);
    List<AdmissionEntity> findByStatus(String status);
}
