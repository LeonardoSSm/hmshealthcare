package com.medicore.application.user;

import com.medicore.domain.shared.DomainException;
import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserManagementService {
    private static final Set<String> ALLOWED_ROLES = Set.of("ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST");

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserJpaRepository userJpaRepository, PasswordEncoder passwordEncoder) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<StaffUserResponse> listAll() {
        return userJpaRepository.findAllByOrderByNameAsc().stream().map(StaffUserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<StaffUserResponse> listActiveDoctors() {
        return userJpaRepository.findByRoleAndActiveTrueOrderByNameAsc("DOCTOR").stream()
            .map(StaffUserResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StaffUserResponse> listActiveNurses() {
        return userJpaRepository.findByRoleAndActiveTrueOrderByNameAsc("NURSE").stream()
            .map(StaffUserResponse::from)
            .toList();
    }

    @Transactional
    public StaffUserResponse create(CreateStaffUserCommand command) {
        String email = normalizeEmail(command.email());
        if (userJpaRepository.existsByEmail(email)) {
            throw new DomainException("Email already in use");
        }

        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setName(requireNonBlank(command.name(), "Name cannot be blank"));
        entity.setEmail(email);
        entity.setPasswordHash(passwordEncoder.encode(requirePassword(command.password())));
        entity.setRole(normalizeRole(command.role()));
        entity.setActive(command.active());
        entity.setCreatedAt(LocalDateTime.now());

        return StaffUserResponse.from(userJpaRepository.save(entity));
    }

    @Transactional
    public StaffUserResponse update(UUID id, UpdateStaffUserCommand command) {
        UserEntity entity = userJpaRepository.findById(id.toString())
            .orElseThrow(() -> new DomainException("User not found"));

        String email = normalizeEmail(command.email());
        if (!entity.getEmail().equalsIgnoreCase(email) && userJpaRepository.existsByEmail(email)) {
            throw new DomainException("Email already in use");
        }

        entity.setName(requireNonBlank(command.name(), "Name cannot be blank"));
        entity.setEmail(email);
        entity.setRole(normalizeRole(command.role()));
        entity.setActive(command.active());
        if (command.password() != null && !command.password().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(requirePassword(command.password())));
        }

        return StaffUserResponse.from(userJpaRepository.save(entity));
    }

    private static String normalizeEmail(String email) {
        String normalized = requireNonBlank(email, "Email cannot be blank").toLowerCase();
        if (!normalized.contains("@") || normalized.endsWith("@")) {
            throw new DomainException("Invalid email");
        }
        return normalized;
    }

    private static String normalizeRole(String role) {
        String normalized = requireNonBlank(role, "Role cannot be blank").toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        if (!ALLOWED_ROLES.contains(normalized)) {
            throw new DomainException("Invalid role");
        }
        return normalized;
    }

    private static String requirePassword(String password) {
        String normalized = requireNonBlank(password, "Password cannot be blank");
        if (normalized.length() < 8) {
            throw new DomainException("Password must have at least 8 characters");
        }
        return normalized;
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }
}
