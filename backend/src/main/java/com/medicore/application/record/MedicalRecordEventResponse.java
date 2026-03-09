package com.medicore.application.record;

import com.medicore.domain.record.MedicalRecordEvent;

import java.time.LocalDateTime;

public record MedicalRecordEventResponse(
    String type,
    String author,
    String description,
    String notes,
    LocalDateTime occurredAt
) {
    public static MedicalRecordEventResponse from(MedicalRecordEvent event) {
        return new MedicalRecordEventResponse(
            event.type().name(),
            event.author(),
            event.description(),
            event.notes(),
            event.occurredAt()
        );
    }
}
