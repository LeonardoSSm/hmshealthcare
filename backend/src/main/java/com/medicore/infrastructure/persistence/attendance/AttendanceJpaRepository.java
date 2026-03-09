package com.medicore.infrastructure.persistence.attendance;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AttendanceJpaRepository extends JpaRepository<AttendanceEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AttendanceEntity a where a.id = :id")
    Optional<AttendanceEntity> findByIdForUpdate(@Param("id") String id);

    boolean existsByPatientIdAndStatusIn(String patientId, Collection<String> statuses);

    List<AttendanceEntity> findByStatusInOrderByPriorityScoreAscCheckInAtAsc(Collection<String> statuses);

    List<AttendanceEntity> findAllByOrderByCheckInAtDesc();
}
