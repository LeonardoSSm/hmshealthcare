package com.medicore.infrastructure.persistence.record;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class MedicalRecordRepositoryImpl implements MedicalRecordRepository {
    private final MedicalRecordJpaRepository medicalRecordJpaRepository;

    public MedicalRecordRepositoryImpl(MedicalRecordJpaRepository medicalRecordJpaRepository) {
        this.medicalRecordJpaRepository = medicalRecordJpaRepository;
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        MedicalRecordEntity saved = medicalRecordJpaRepository.save(MedicalRecordMapper.toEntity(medicalRecord));
        return MedicalRecordMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> findById(UUID id) {
        return medicalRecordJpaRepository.findById(id.toString()).map(MedicalRecordMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> findByPatientId(PatientId patientId) {
        return medicalRecordJpaRepository.findByPatientId(patientId.value().toString()).map(MedicalRecordMapper::toDomain);
    }
}
