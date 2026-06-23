package com.medicore.infrastructure.persistence.patient;

import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.CPF;
import com.medicore.domain.shared.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepositoryImpl implements PatientRepository {
    private final PatientJpaRepository patientJpaRepository;

    public PatientRepositoryImpl(PatientJpaRepository patientJpaRepository) {
        this.patientJpaRepository = patientJpaRepository;
    }

    @Override
    public Patient save(Patient patient) {
        PatientEntity saved = patientJpaRepository.save(PatientMapper.toEntity(patient));
        return PatientMapper.toDomain(saved);
    }

    @Override
    public Optional<Patient> findById(PatientId id) {
        return patientJpaRepository.findById(id.value().toString()).map(PatientMapper::toDomain);
    }

    @Override
    public Optional<Patient> findByCpf(CPF cpf) {
        return patientJpaRepository.findByCpf(cpf.value()).map(PatientMapper::toDomain);
    }

    @Override
    public List<Patient> search(String query) {
        String safe = query == null ? "" : query.trim();
        return patientJpaRepository.findByNameContainingIgnoreCaseOrCpfContaining(safe, safe)
            .stream()
            .map(PatientMapper::toDomain)
            .toList();
    }

    @Override
    public PagedResult<Patient> searchPaged(String query, int page, int size) {
        String safe = query == null ? "" : query.trim();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<PatientEntity> result = patientJpaRepository
            .findByNameContainingIgnoreCaseOrCpfContaining(safe, safe, pageable);
        return new PagedResult<>(
            result.getContent().stream().map(PatientMapper::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }
}
