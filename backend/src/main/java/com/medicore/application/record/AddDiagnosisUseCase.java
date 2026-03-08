package com.medicore.application.record;

import com.medicore.domain.record.Diagnosis;
import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AddDiagnosisUseCase {
    private final MedicalRecordRepository medicalRecordRepository;

    public AddDiagnosisUseCase(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional
    public MedicalRecordResponse execute(UUID medicalRecordId, DiagnosisRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
            .orElseThrow(() -> new DomainException("Medical record not found"));

        Diagnosis diagnosis = new Diagnosis(
            request.doctorId(),
            request.icd10Code(),
            request.description(),
            request.notes(),
            request.diagnosedAt() == null ? LocalDateTime.now() : request.diagnosedAt()
        );
        record.addDiagnosis(diagnosis);

        MedicalRecord saved = medicalRecordRepository.save(record);
        return MedicalRecordResponse.from(saved);
    }
}
