package com.medicore.application.patient;

import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeactivatePatientUseCase {
    private final PatientRepository patientRepository;

    public DeactivatePatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public void execute(UUID patientId) {
        Patient patient = patientRepository.findById(new PatientId(patientId))
            .orElseThrow(() -> new DomainException("Patient not found"));
        patient.deactivate();
        patientRepository.save(patient);
    }
}
