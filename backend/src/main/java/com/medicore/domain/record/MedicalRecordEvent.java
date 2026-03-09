package com.medicore.domain.record;

import com.medicore.domain.shared.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;

public record MedicalRecordEvent(
    ClinicalEventType type,
    String author,
    String description,
    String notes,
    LocalDateTime occurredAt
) {
    public MedicalRecordEvent {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (author == null || author.isBlank()) {
            throw new DomainException("Author cannot be blank");
        }
        if (description == null || description.isBlank()) {
            throw new DomainException("Description cannot be blank");
        }
        author = author.trim();
        description = description.trim();
        notes = notes == null ? "" : notes.trim();
    }
}
