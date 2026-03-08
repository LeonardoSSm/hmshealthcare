package com.medicore.domain.patient;

import java.util.Objects;
import java.util.UUID;

public record PatientId(UUID value) {
    public PatientId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static PatientId newId() {
        return new PatientId(UUID.randomUUID());
    }
}
