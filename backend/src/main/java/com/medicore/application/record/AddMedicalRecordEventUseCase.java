package com.medicore.application.record;

import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordEvent;
import com.medicore.domain.record.MedicalRecordRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AddMedicalRecordEventUseCase {
    private final MedicalRecordRepository medicalRecordRepository;

    public AddMedicalRecordEventUseCase(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional
    public MedicalRecordResponse execute(UUID medicalRecordId, MedicalRecordEventRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
            .orElseThrow(() -> new DomainException("Medical record not found"));

        MedicalRecordEvent event = new MedicalRecordEvent(
            request.type(),
            request.author(),
            request.description(),
            request.notes(),
            request.occurredAt() == null ? LocalDateTime.now() : request.occurredAt()
        );
        record.addEvent(event);

        MedicalRecord saved = medicalRecordRepository.save(record);
        return MedicalRecordResponse.from(saved);
    }
}
