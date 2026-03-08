package com.medicore.domain.record;

import com.medicore.domain.shared.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public record Diagnosis(
    UUID doctorId,
    String icd10Code,
    String description,
    String notes,
    LocalDateTime diagnosedAt
) {
    private static final Pattern ICD10 = Pattern.compile("^[A-TV-Z][0-9][0-9AB](\\.[0-9A-TV-Z]{1,4})?$");

    public Diagnosis {
        Objects.requireNonNull(doctorId, "doctorId cannot be null");
        Objects.requireNonNull(diagnosedAt, "diagnosedAt cannot be null");
        if (icd10Code == null || !ICD10.matcher(icd10Code.toUpperCase()).matches()) {
            throw new DomainException("Invalid ICD-10 code");
        }
        if (description == null || description.isBlank()) {
            throw new DomainException("Description cannot be blank");
        }
        icd10Code = icd10Code.toUpperCase();
        description = description.trim();
        notes = notes == null ? "" : notes.trim();
    }
}
