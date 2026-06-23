package com.medicore.application.record;

import com.medicore.domain.record.Prescription;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrescriptionResponse(
    UUID doctorId,
    String medication,
    String dosage,
    String frequency,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
    public static PrescriptionResponse from(Prescription p) {
        return new PrescriptionResponse(
            p.doctorId(),
            p.medication(),
            p.dosage(),
            p.frequency(),
            p.period().start(),
            p.period().end()
        );
    }
}
