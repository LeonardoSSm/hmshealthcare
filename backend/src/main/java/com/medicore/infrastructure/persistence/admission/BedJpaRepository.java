package com.medicore.infrastructure.persistence.admission;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BedJpaRepository extends JpaRepository<BedEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BedEntity b where b.id = :id")
    Optional<BedEntity> findByIdForUpdate(@Param("id") String id);
}
