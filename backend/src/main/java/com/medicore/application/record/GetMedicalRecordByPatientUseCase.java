package com.medicore.application.record;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetMedicalRecordByPatientUseCase {
    private final MedicalRecordRepository medicalRecordRepository;

    public GetMedicalRecordByPatientUseCase(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public MedicalRecordResponse execute(UUID patientId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(new PatientId(patientId))
            .orElseThrow(() -> new DomainException("Medical record not found"));
        return MedicalRecordResponse.from(medicalRecord);
    }
}
