package com.medicore.infrastructure.persistence.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.attendance.AttendanceStatus;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.shared.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class AttendanceRepositoryImpl implements AttendanceRepository {
    private static final List<String> OPEN_STATUSES = List.of(
        AttendanceStatus.WAITING_TRIAGE.name(),
        AttendanceStatus.IN_TRIAGE.name(),
        AttendanceStatus.WAITING_DOCTOR.name(),
        AttendanceStatus.CALLED_DOCTOR.name(),
        AttendanceStatus.IN_CONSULTATION.name()
    );

    private final AttendanceJpaRepository attendanceJpaRepository;

    public AttendanceRepositoryImpl(AttendanceJpaRepository attendanceJpaRepository) {
        this.attendanceJpaRepository = attendanceJpaRepository;
    }

    @Override
    public Attendance save(Attendance attendance) {
        AttendanceEntity saved = attendanceJpaRepository.save(AttendanceMapper.toEntity(attendance));
        return AttendanceMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findById(UUID id) {
        return attendanceJpaRepository.findById(id.toString()).map(AttendanceMapper::toDomain);
    }

    @Override
    public Optional<Attendance> findByIdForUpdate(UUID id) {
        return attendanceJpaRepository.findByIdForUpdate(id.toString()).map(AttendanceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findOpenAttendances() {
        return attendanceJpaRepository.findByStatusInOrderByPriorityScoreAscCheckInAtAsc(OPEN_STATUSES).stream()
            .map(AttendanceMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findAll() {
        return attendanceJpaRepository.findAllByOrderByCheckInAtDesc().stream()
            .map(AttendanceMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Attendance> findAllPaged(int page, int size) {
        Page<AttendanceEntity> result = attendanceJpaRepository
            .findAllByOrderByCheckInAtDesc(PageRequest.of(page, size));
        return new PagedResult<>(
            result.getContent().stream().map(AttendanceMapper::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Attendance> findByDateRange(LocalDate from, LocalDate to, int page, int size) {
        Page<AttendanceEntity> result = attendanceJpaRepository.findByDateRange(
            from.atStartOfDay(),
            to.plusDays(1).atStartOfDay(),
            PageRequest.of(page, size)
        );
        return new PagedResult<>(
            result.getContent().stream().map(AttendanceMapper::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOpenByPatientId(PatientId patientId) {
        return attendanceJpaRepository.existsByPatientIdAndStatusIn(patientId.value().toString(), OPEN_STATUSES);
    }
}
