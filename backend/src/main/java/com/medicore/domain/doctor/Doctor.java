package com.medicore.domain.doctor;

import com.medicore.domain.shared.CRM;

import java.util.Objects;
import java.util.UUID;

public class Doctor {
    private final UUID id;
    private final UUID userId;
    private final CRM crm;
    private final String specialty;

    public Doctor(UUID id, UUID userId, CRM crm, String specialty) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.crm = Objects.requireNonNull(crm, "crm cannot be null");
        this.specialty = specialty == null ? "" : specialty.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public CRM getCrm() {
        return crm;
    }

    public String getSpecialty() {
        return specialty;
    }
}
