package com.medicore.domain.record;

import com.medicore.domain.patient.PatientId;

import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository {
    MedicalRecord save(MedicalRecord medicalRecord);
    Optional<MedicalRecord> findById(UUID id);
    Optional<MedicalRecord> findByPatientId(PatientId patientId);
}
