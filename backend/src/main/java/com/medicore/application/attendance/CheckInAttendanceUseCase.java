package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInAttendanceUseCase {
    private final AttendanceRepository attendanceRepository;
    private final PatientRepository patientRepository;
    private final AttendanceTicketGenerator ticketGenerator;
    private final AttendanceResponseMapper responseMapper;

    public CheckInAttendanceUseCase(
        AttendanceRepository attendanceRepository,
        PatientRepository patientRepository,
        AttendanceTicketGenerator ticketGenerator,
        AttendanceResponseMapper responseMapper
    ) {
        this.attendanceRepository = attendanceRepository;
        this.patientRepository = patientRepository;
        this.ticketGenerator = ticketGenerator;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public AttendanceResponse execute(CheckInAttendanceCommand command) {
        PatientId patientId = new PatientId(command.patientId());
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new DomainException("Patient not found"));
        if (!patient.isActive()) {
            throw new DomainException("Inactive patient cannot be checked in");
        }
        if (attendanceRepository.existsOpenByPatientId(patientId)) {
            throw new DomainException("Patient already has an active attendance flow");
        }

        String requestedBy = command.requestedBy() == null || command.requestedBy().isBlank()
            ? "Recepcao"
            : command.requestedBy().trim();
        Attendance attendance = Attendance.checkIn(patientId, ticketGenerator.generate(), command.notes(), requestedBy);
        Attendance saved = attendanceRepository.save(attendance);
        return responseMapper.toResponse(saved);
    }
}
