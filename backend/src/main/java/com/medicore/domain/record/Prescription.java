package com.medicore.domain.record;

import com.medicore.domain.shared.DateRange;
import com.medicore.domain.shared.DomainException;

import java.util.Objects;
import java.util.UUID;

public record Prescription(
    UUID doctorId,
    String medication,
    String dosage,
    String frequency,
    DateRange period
) {
    public Prescription {
        Objects.requireNonNull(doctorId, "doctorId cannot be null");
        Objects.requireNonNull(period, "period cannot be null");
        if (medication == null || medication.isBlank()) {
            throw new DomainException("Medication cannot be blank");
        }
        if (dosage == null || dosage.isBlank()) {
            throw new DomainException("Dosage cannot be blank");
        }
        if (frequency == null || frequency.isBlank()) {
            throw new DomainException("Frequency cannot be blank");
        }
        medication = medication.trim();
        dosage = dosage.trim();
        frequency = frequency.trim();
    }
}
