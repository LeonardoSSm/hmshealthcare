package com.medicore.infrastructure.persistence.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.admission.AdmissionStatus;
import com.medicore.domain.patient.PatientId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AdmissionRepositoryImpl implements AdmissionRepository {
    private final AdmissionJpaRepository admissionJpaRepository;

    public AdmissionRepositoryImpl(AdmissionJpaRepository admissionJpaRepository) {
        this.admissionJpaRepository = admissionJpaRepository;
    }

    @Override
    public Admission save(Admission admission) {
        AdmissionEntity saved = admissionJpaRepository.save(AdmissionMapper.toEntity(admission));
        return AdmissionMapper.toDomain(saved);
    }

    @Override
    public Optional<Admission> findById(UUID id) {
        return admissionJpaRepository.findById(id.toString()).map(AdmissionMapper::toDomain);
    }

    @Override
    public List<Admission> findByPatientId(PatientId patientId) {
        return admissionJpaRepository.findByPatientId(patientId.value().toString()).stream().map(AdmissionMapper::toDomain).toList();
    }

    @Override
    public List<Admission> findActiveAdmissions() {
        return admissionJpaRepository.findByStatus(AdmissionStatus.ACTIVE.name()).stream().map(AdmissionMapper::toDomain).toList();
    }

    @Override
    public List<Admission> findAll() {
        return admissionJpaRepository.findAllByOrderByAdmissionDateDesc().stream().map(AdmissionMapper::toDomain).toList();
    }
}
