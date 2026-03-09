package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import org.springframework.stereotype.Component;

@Component
public class AttendanceResponseMapper {
    private final PatientRepository patientRepository;

    public AttendanceResponseMapper(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public AttendanceResponse toResponse(Attendance attendance) {
        String patientName = patientRepository.findById(new PatientId(attendance.getPatientId().value()))
            .map(Patient::getName)
            .orElse("Paciente nao encontrado");
        return AttendanceResponse.from(attendance, patientName);
    }

    public QueuePanelItemResponse toPanelItem(Attendance attendance) {
        String displayName = patientRepository.findById(new PatientId(attendance.getPatientId().value()))
            .map(Patient::getName)
            .map(AttendanceResponseMapper::maskName)
            .orElse("Paciente");
        return new QueuePanelItemResponse(
            attendance.getId(),
            attendance.getTicketNumber(),
            displayName,
            attendance.getStatus().name(),
            attendance.getRiskLevel() == null ? null : attendance.getRiskLevel().name(),
            attendance.getRoomLabel(),
            attendance.getCalledAt(),
            attendance.getCheckInAt()
        );
    }

    private static String maskName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0) {
            return "Paciente";
        }
        if (parts.length == 1) {
            return parts[0];
        }
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + ".";
    }
}
