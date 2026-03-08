package com.medicore.domain.shared;

import java.time.LocalDateTime;
import java.util.Objects;

public record DateRange(LocalDateTime start, LocalDateTime end) {
    public DateRange {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        if (!end.isAfter(start)) {
            throw new DomainException("end must be after start");
        }
    }
}
