package com.medicore.infrastructure.persistence.token;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class RefreshTokenCleanupJob {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenCleanupJob(RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    // runs every day at 03:00
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpiredAndRevoked() {
        int deleted = refreshTokenJpaRepository.deleteExpiredAndRevoked(LocalDateTime.now());
        if (deleted > 0) {
            // log visible in app logs without requiring a logger field
            System.out.printf("[RefreshTokenCleanupJob] Purged %d expired/revoked tokens%n", deleted);
        }
    }
}
