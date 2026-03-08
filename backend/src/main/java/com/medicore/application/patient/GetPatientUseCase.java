package com.medicore.application.patient;

import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPatientUseCase {
    private final PatientRepository patientRepository;

    public GetPatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponse execute(UUID patientId) {
        Patient patient = patientRepository.findById(new PatientId(patientId))
            .orElseThrow(() -> new DomainException("Patient not found"));
        return PatientResponseMapper.toResponse(patient);
    }
}
