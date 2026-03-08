package com.medicore.domain.admission;

import com.medicore.domain.patient.PatientId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdmissionRepository {
    Admission save(Admission admission);
    Optional<Admission> findById(UUID id);
    List<Admission> findByPatientId(PatientId patientId);
    List<Admission> findActiveAdmissions();
}
