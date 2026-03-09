package com.medicore.domain.attendance;

import com.medicore.domain.patient.PatientId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);
    Optional<Attendance> findById(UUID id);
    Optional<Attendance> findByIdForUpdate(UUID id);
    List<Attendance> findOpenAttendances();
    List<Attendance> findAll();
    boolean existsOpenByPatientId(PatientId patientId);
}
