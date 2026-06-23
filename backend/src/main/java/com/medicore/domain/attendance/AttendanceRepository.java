package com.medicore.domain.attendance;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.shared.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);
    Optional<Attendance> findById(UUID id);
    Optional<Attendance> findByIdForUpdate(UUID id);
    List<Attendance> findOpenAttendances();
    List<Attendance> findAll();
    PagedResult<Attendance> findAllPaged(int page, int size);
    boolean existsOpenByPatientId(PatientId patientId);
}
