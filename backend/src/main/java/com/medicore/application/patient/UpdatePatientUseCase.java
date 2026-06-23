package com.medicore.application.patient;

import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.BloodType;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdatePatientUseCase {
    private final PatientRepository patientRepository;

    public UpdatePatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public PatientResponse execute(UUID patientId, UpdatePatientCommand command) {
        Patient patient = patientRepository.findById(new PatientId(patientId))
            .orElseThrow(() -> new DomainException("Patient not found"));

        patient.update(
            command.name(),
            command.birthDate(),
            BloodType.valueOf(command.bloodType().trim().toUpperCase()),
            command.allergies(),
            command.phone(),
            command.email(),
            command.address()
        );

        return PatientResponseMapper.toResponse(patientRepository.save(patient));
    }
}
