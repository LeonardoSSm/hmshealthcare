package com.medicore.application.record;

import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.MedicalRecordRepository;
import com.medicore.domain.record.Prescription;
import com.medicore.domain.shared.DateRange;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AddPrescriptionUseCase {
    private final MedicalRecordRepository medicalRecordRepository;

    public AddPrescriptionUseCase(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional
    public MedicalRecordResponse execute(UUID medicalRecordId, PrescriptionRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(medicalRecordId)
            .orElseThrow(() -> new DomainException("Medical record not found"));

        Prescription prescription = new Prescription(
            request.doctorId(),
            request.medication(),
            request.dosage(),
            request.frequency(),
            new DateRange(request.startDate(), request.endDate())
        );
        record.addPrescription(prescription);

        return MedicalRecordResponse.from(medicalRecordRepository.save(record));
    }
}
