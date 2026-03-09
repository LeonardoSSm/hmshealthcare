package com.medicore.application.record;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetMedicalRecordByPatientUseCase {
    private final MedicalRecordRepository medicalRecordRepository;

    public GetMedicalRecordByPatientUseCase(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public MedicalRecordResponse execute(UUID patientId) {
        PatientId targetPatientId = new PatientId(patientId);
        MedicalRecord medicalRecord = medicalRecordRepository.findByPatientId(targetPatientId)
            .orElseGet(() -> medicalRecordRepository.save(MedicalRecord.createFor(targetPatientId)));
        return MedicalRecordResponse.from(medicalRecord);
    }
}
