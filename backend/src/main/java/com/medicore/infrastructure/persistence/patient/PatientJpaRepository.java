package com.medicore.infrastructure.persistence.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, String> {
    Optional<PatientEntity> findByCpf(String cpf);
    List<PatientEntity> findByNameContainingIgnoreCaseOrCpfContaining(String name, String cpf);
}
