package com.medicore.application.attendance;

import com.medicore.domain.shared.DomainException;
import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AttendanceStaffValidator {
    private final UserJpaRepository userJpaRepository;

    public AttendanceStaffValidator(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    public void ensureActiveDoctor(UUID userId) {
        UserEntity user = userJpaRepository.findById(userId.toString())
            .orElseThrow(() -> new DomainException("Doctor not found"));
        if (!user.isActive() || !"DOCTOR".equals(normalizeRole(user.getRole()))) {
            throw new DomainException("Responsible professional must be an active doctor");
        }
    }

    public void ensureActiveNurse(UUID userId) {
        UserEntity user = userJpaRepository.findById(userId.toString())
            .orElseThrow(() -> new DomainException("Nurse not found"));
        if (!user.isActive() || !"NURSE".equals(normalizeRole(user.getRole()))) {
            throw new DomainException("Responsible professional must be an active nurse");
        }
    }

    private static String normalizeRole(String rawRole) {
        String normalized = rawRole == null ? "" : rawRole.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }
}
