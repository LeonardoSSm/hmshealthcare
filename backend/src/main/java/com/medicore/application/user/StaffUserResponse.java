package com.medicore.application.user;

import com.medicore.infrastructure.persistence.user.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record StaffUserResponse(
    UUID id,
    String name,
    String email,
    String role,
    boolean active,
    LocalDateTime createdAt
) {
    public static StaffUserResponse from(UserEntity user) {
        return new StaffUserResponse(
            UUID.fromString(user.getId()),
            user.getName(),
            user.getEmail(),
            normalizeRole(user.getRole()),
            user.isActive(),
            user.getCreatedAt()
        );
    }

    private static String normalizeRole(String rawRole) {
        String normalized = rawRole == null ? "" : rawRole.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }
}
