package com.medicore.application.patient;

import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordRepository;
import com.medicore.domain.shared.BloodType;
import com.medicore.domain.shared.CPF;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegisterPatientUseCase {
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public RegisterPatientUseCase(PatientRepository patientRepository, MedicalRecordRepository medicalRecordRepository) {
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional
    public PatientResponse execute(RegisterPatientCommand command) {
        CPF cpf = new CPF(command.cpf());
        patientRepository.findByCpf(cpf).ifPresent(existing -> {
            throw new DomainException("Patient with this CPF already exists");
        });

        Patient patient = Patient.create(
            command.name(),
            cpf,
            command.birthDate(),
            BloodType.valueOf(command.bloodType().trim().toUpperCase()),
            command.allergies(),
            command.phone(),
            command.email(),
            command.address()
        );

        Patient saved = patientRepository.save(patient);
        MedicalRecord record = MedicalRecord.createFor(saved.getId());
        medicalRecordRepository.save(record);

        return PatientResponseMapper.toResponse(saved);
    }
}
