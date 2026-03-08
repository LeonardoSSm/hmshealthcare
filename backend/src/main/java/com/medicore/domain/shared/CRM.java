package com.medicore.domain.shared;

import java.util.Objects;
import java.util.regex.Pattern;

public final class CRM {
    private static final Pattern PATTERN = Pattern.compile("^[A-Z]{2}\\d{4,10}$");

    private final String value;

    public CRM(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new DomainException("CRM cannot be blank");
        }
        String normalized = rawValue.toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (!PATTERN.matcher(normalized).matches()) {
            throw new DomainException("Invalid CRM format");
        }
        this.value = normalized;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CRM crm)) {
            return false;
        }
        return Objects.equals(value, crm.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
