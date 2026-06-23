package com.medicore.infrastructure.persistence.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByTokenHashAndRevokedFalse(String tokenHash);
    List<RefreshTokenEntity> findByUserIdAndRevokedFalse(String userId);

    @Modifying
    @Query("delete from RefreshTokenEntity t where t.expiresAt < :cutoff or t.revoked = true")
    int deleteExpiredAndRevoked(@Param("cutoff") LocalDateTime cutoff);
}
