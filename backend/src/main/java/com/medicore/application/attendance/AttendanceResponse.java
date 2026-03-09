package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttendanceResponse(
    UUID id,
    String ticketNumber,
    UUID patientId,
    String patientName,
    String status,
    String riskLevel,
    int priorityScore,
    UUID nurseId,
    UUID doctorId,
    String roomLabel,
    String checkInNotes,
    String triageNotes,
    String consultationNotes,
    String outcome,
    LocalDateTime checkInAt,
    LocalDateTime triageStartedAt,
    LocalDateTime triageFinishedAt,
    LocalDateTime calledAt,
    LocalDateTime consultationStartedAt,
    LocalDateTime consultationFinishedAt,
    LocalDateTime updatedAt
) {
    public static AttendanceResponse from(Attendance attendance, String patientName) {
        return new AttendanceResponse(
            attendance.getId(),
            attendance.getTicketNumber(),
            attendance.getPatientId().value(),
            patientName,
            attendance.getStatus().name(),
            attendance.getRiskLevel() == null ? null : attendance.getRiskLevel().name(),
            attendance.getPriorityScore(),
            attendance.getNurseId(),
            attendance.getDoctorId(),
            attendance.getRoomLabel(),
            attendance.getCheckInNotes(),
            attendance.getTriageNotes(),
            attendance.getConsultationNotes(),
            attendance.getOutcome(),
            attendance.getCheckInAt(),
            attendance.getTriageStartedAt(),
            attendance.getTriageFinishedAt(),
            attendance.getCalledAt(),
            attendance.getConsultationStartedAt(),
            attendance.getConsultationFinishedAt(),
            attendance.getUpdatedAt()
        );
    }
}
