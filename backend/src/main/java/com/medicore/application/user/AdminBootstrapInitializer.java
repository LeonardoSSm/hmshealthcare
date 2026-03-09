package com.medicore.application.user;

import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AdminBootstrapInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapInitializer.class);

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String name;
    private final String email;
    private final String password;

    public AdminBootstrapInitializer(
        UserJpaRepository userJpaRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.bootstrap-admin.enabled:false}") boolean enabled,
        @Value("${app.bootstrap-admin.name:Platform Admin}") String name,
        @Value("${app.bootstrap-admin.email:}") String email,
        @Value("${app.bootstrap-admin.password:}") String password
    ) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }

        String normalizedEmail = normalizeEmail(email);
        String normalizedName = name == null || name.isBlank() ? "Platform Admin" : name.trim();
        String normalizedPassword = requireStrongPassword(password);

        UserEntity admin = userJpaRepository.findByEmail(normalizedEmail).orElseGet(() -> {
            UserEntity created = new UserEntity();
            created.setId(UUID.randomUUID().toString());
            created.setCreatedAt(LocalDateTime.now());
            return created;
        });

        admin.setName(normalizedName);
        admin.setEmail(normalizedEmail);
        admin.setRole("ADMIN");
        admin.setActive(true);
        admin.setPasswordHash(passwordEncoder.encode(normalizedPassword));
        userJpaRepository.save(admin);

        log.info("Bootstrap admin account ensured for email {}", normalizedEmail);
    }

    private static String normalizeEmail(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("APP_BOOTSTRAP_ADMIN_EMAIL must be defined when bootstrap admin is enabled");
        }
        String normalized = value.trim().toLowerCase();
        if (!normalized.contains("@") || normalized.endsWith("@")) {
            throw new IllegalStateException("APP_BOOTSTRAP_ADMIN_EMAIL is invalid");
        }
        return normalized;
    }

    private static String requireStrongPassword(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("APP_BOOTSTRAP_ADMIN_PASSWORD must be defined when bootstrap admin is enabled");
        }
        if (value.length() < 12) {
            throw new IllegalStateException("APP_BOOTSTRAP_ADMIN_PASSWORD must have at least 12 characters");
        }
        return value;
    }
}
