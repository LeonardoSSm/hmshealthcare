package com.medicore.infrastructure.persistence.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceStatus;
import com.medicore.domain.attendance.AttendanceStatusChange;
import com.medicore.domain.attendance.RiskLevel;
import com.medicore.domain.patient.PatientId;

import java.util.List;
import java.util.UUID;

public final class AttendanceMapper {
    private AttendanceMapper() {
    }

    public static AttendanceEntity toEntity(Attendance attendance) {
        AttendanceEntity entity = new AttendanceEntity();
        entity.setId(attendance.getId().toString());
        entity.setTicketNumber(attendance.getTicketNumber());
        entity.setPatientId(attendance.getPatientId().value().toString());
        entity.setStatus(attendance.getStatus().name());
        entity.setRiskLevel(attendance.getRiskLevel() == null ? null : attendance.getRiskLevel().name());
        entity.setPriorityScore(attendance.getPriorityScore());
        entity.setNurseId(attendance.getNurseId() == null ? null : attendance.getNurseId().toString());
        entity.setDoctorId(attendance.getDoctorId() == null ? null : attendance.getDoctorId().toString());
        entity.setRoomLabel(attendance.getRoomLabel());
        entity.setCheckInNotes(attendance.getCheckInNotes());
        entity.setTriageNotes(attendance.getTriageNotes());
        entity.setConsultationNotes(attendance.getConsultationNotes());
        entity.setOutcome(attendance.getOutcome());
        entity.setCheckInAt(attendance.getCheckInAt());
        entity.setTriageStartedAt(attendance.getTriageStartedAt());
        entity.setTriageFinishedAt(attendance.getTriageFinishedAt());
        entity.setCalledAt(attendance.getCalledAt());
        entity.setConsultationStartedAt(attendance.getConsultationStartedAt());
        entity.setConsultationFinishedAt(attendance.getConsultationFinishedAt());
        entity.setCreatedAt(attendance.getCreatedAt());
        entity.setUpdatedAt(attendance.getUpdatedAt());
        entity.setVersion(attendance.getVersion());

        List<AttendanceStatusHistoryEntity> history = attendance.getStatusHistory().stream().map(item -> {
            AttendanceStatusHistoryEntity he = new AttendanceStatusHistoryEntity();
            he.setAttendance(entity);
            he.setFromStatus(item.fromStatus() == null ? null : item.fromStatus().name());
            he.setToStatus(item.toStatus().name());
            he.setChangedBy(item.changedBy());
            he.setNote(item.note());
            he.setChangedAt(item.changedAt());
            return he;
        }).toList();

        entity.setStatusHistory(history);
        return entity;
    }

    public static Attendance toDomain(AttendanceEntity entity) {
        List<AttendanceStatusChange> history = entity.getStatusHistory().stream()
            .map(item -> new AttendanceStatusChange(
                item.getFromStatus() == null ? null : AttendanceStatus.valueOf(item.getFromStatus()),
                AttendanceStatus.valueOf(item.getToStatus()),
                item.getChangedBy(),
                item.getNote(),
                item.getChangedAt()
            ))
            .toList();

        return Attendance.rehydrate(
            UUID.fromString(entity.getId()),
            entity.getTicketNumber(),
            new PatientId(UUID.fromString(entity.getPatientId())),
            AttendanceStatus.valueOf(entity.getStatus()),
            entity.getRiskLevel() == null ? null : RiskLevel.valueOf(entity.getRiskLevel()),
            entity.getPriorityScore(),
            entity.getNurseId() == null ? null : UUID.fromString(entity.getNurseId()),
            entity.getDoctorId() == null ? null : UUID.fromString(entity.getDoctorId()),
            entity.getRoomLabel(),
            entity.getCheckInNotes(),
            entity.getTriageNotes(),
            entity.getConsultationNotes(),
            entity.getOutcome(),
            entity.getCheckInAt(),
            entity.getTriageStartedAt(),
            entity.getTriageFinishedAt(),
            entity.getCalledAt(),
            entity.getConsultationStartedAt(),
            entity.getConsultationFinishedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getVersion(),
            history
        );
    }
}
