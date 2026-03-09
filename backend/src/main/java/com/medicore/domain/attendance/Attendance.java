package com.medicore.domain.attendance;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.shared.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Attendance {
    private final UUID id;
    private final String ticketNumber;
    private final PatientId patientId;
    private final LocalDateTime createdAt;

    private AttendanceStatus status;
    private RiskLevel riskLevel;
    private int priorityScore;
    private UUID nurseId;
    private UUID doctorId;
    private String roomLabel;
    private String checkInNotes;
    private String triageNotes;
    private String consultationNotes;
    private String outcome;
    private LocalDateTime checkInAt;
    private LocalDateTime triageStartedAt;
    private LocalDateTime triageFinishedAt;
    private LocalDateTime calledAt;
    private LocalDateTime consultationStartedAt;
    private LocalDateTime consultationFinishedAt;
    private LocalDateTime updatedAt;
    private long version;
    private final List<AttendanceStatusChange> statusHistory;

    private Attendance(
        UUID id,
        String ticketNumber,
        PatientId patientId,
        AttendanceStatus status,
        RiskLevel riskLevel,
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
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long version,
        List<AttendanceStatusChange> statusHistory
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.ticketNumber = requireTicket(ticketNumber);
        this.patientId = Objects.requireNonNull(patientId, "patientId cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.riskLevel = riskLevel;
        this.priorityScore = priorityScore <= 0 ? 999 : priorityScore;
        this.nurseId = nurseId;
        this.doctorId = doctorId;
        this.roomLabel = trimNullable(roomLabel);
        this.checkInNotes = trimNullable(checkInNotes);
        this.triageNotes = trimNullable(triageNotes);
        this.consultationNotes = trimNullable(consultationNotes);
        this.outcome = trimNullable(outcome);
        this.checkInAt = Objects.requireNonNull(checkInAt, "checkInAt cannot be null");
        this.triageStartedAt = triageStartedAt;
        this.triageFinishedAt = triageFinishedAt;
        this.calledAt = calledAt;
        this.consultationStartedAt = consultationStartedAt;
        this.consultationFinishedAt = consultationFinishedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
        this.version = version;
        this.statusHistory = new ArrayList<>(statusHistory == null ? List.of() : statusHistory);
    }

    public static Attendance checkIn(PatientId patientId, String ticketNumber, String checkInNotes, String requestedBy) {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = new Attendance(
            UUID.randomUUID(),
            ticketNumber,
            patientId,
            AttendanceStatus.WAITING_TRIAGE,
            null,
            999,
            null,
            null,
            null,
            checkInNotes,
            null,
            null,
            null,
            now,
            null,
            null,
            null,
            null,
            null,
            now,
            now,
            0L,
            List.of()
        );
        attendance.addHistory(null, AttendanceStatus.WAITING_TRIAGE, requestedBy, "Check-in realizado", now);
        return attendance;
    }

    public static Attendance rehydrate(
        UUID id,
        String ticketNumber,
        PatientId patientId,
        AttendanceStatus status,
        RiskLevel riskLevel,
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
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long version,
        List<AttendanceStatusChange> statusHistory
    ) {
        return new Attendance(
            id,
            ticketNumber,
            patientId,
            status,
            riskLevel,
            priorityScore,
            nurseId,
            doctorId,
            roomLabel,
            checkInNotes,
            triageNotes,
            consultationNotes,
            outcome,
            checkInAt,
            triageStartedAt,
            triageFinishedAt,
            calledAt,
            consultationStartedAt,
            consultationFinishedAt,
            createdAt,
            updatedAt,
            version,
            statusHistory
        );
    }

    public void startTriage(UUID nurseId, String requestedBy) {
        if (status != AttendanceStatus.WAITING_TRIAGE) {
            throw new DomainException("Attendance is not waiting for triage");
        }
        this.nurseId = Objects.requireNonNull(nurseId, "nurseId cannot be null");
        LocalDateTime now = LocalDateTime.now();
        this.triageStartedAt = now;
        transitionTo(AttendanceStatus.IN_TRIAGE, requestedBy, "Triagem iniciada", now);
    }

    public void finishTriage(UUID nurseId, RiskLevel riskLevel, String triageNotes, String requestedBy) {
        if (status != AttendanceStatus.IN_TRIAGE) {
            throw new DomainException("Attendance is not in triage");
        }
        if (this.nurseId != null && !this.nurseId.equals(nurseId)) {
            throw new DomainException("Only the responsible nurse can finish triage");
        }
        this.nurseId = Objects.requireNonNull(nurseId, "nurseId cannot be null");
        this.riskLevel = Objects.requireNonNull(riskLevel, "riskLevel cannot be null");
        this.priorityScore = riskLevel.priorityScore();
        this.triageNotes = trimNullable(triageNotes);
        LocalDateTime now = LocalDateTime.now();
        this.triageFinishedAt = now;
        transitionTo(AttendanceStatus.WAITING_DOCTOR, requestedBy, "Triagem concluida", now);
    }

    public void callDoctor(UUID doctorId, String roomLabel, String requestedBy) {
        if (status != AttendanceStatus.WAITING_DOCTOR) {
            throw new DomainException("Attendance is not waiting for doctor");
        }
        this.doctorId = Objects.requireNonNull(doctorId, "doctorId cannot be null");
        if (roomLabel == null || roomLabel.isBlank()) {
            throw new DomainException("Room label is required");
        }
        this.roomLabel = roomLabel.trim();
        LocalDateTime now = LocalDateTime.now();
        this.calledAt = now;
        transitionTo(AttendanceStatus.CALLED_DOCTOR, requestedBy, "Paciente chamado para consultorio", now);
    }

    public void startConsultation(UUID doctorId, String requestedBy) {
        if (status != AttendanceStatus.CALLED_DOCTOR) {
            throw new DomainException("Attendance is not called to doctor");
        }
        if (this.doctorId != null && !this.doctorId.equals(doctorId)) {
            throw new DomainException("Only the called doctor can start consultation");
        }
        this.doctorId = Objects.requireNonNull(doctorId, "doctorId cannot be null");
        LocalDateTime now = LocalDateTime.now();
        this.consultationStartedAt = now;
        transitionTo(AttendanceStatus.IN_CONSULTATION, requestedBy, "Consulta iniciada", now);
    }

    public void finishConsultation(UUID doctorId, String outcome, String consultationNotes, String requestedBy) {
        if (status != AttendanceStatus.IN_CONSULTATION) {
            throw new DomainException("Attendance is not in consultation");
        }
        if (this.doctorId != null && !this.doctorId.equals(doctorId)) {
            throw new DomainException("Only the responsible doctor can finish consultation");
        }
        if (outcome == null || outcome.isBlank()) {
            throw new DomainException("Outcome is required");
        }
        this.doctorId = Objects.requireNonNull(doctorId, "doctorId cannot be null");
        this.outcome = outcome.trim();
        this.consultationNotes = trimNullable(consultationNotes);
        LocalDateTime now = LocalDateTime.now();
        this.consultationFinishedAt = now;
        transitionTo(AttendanceStatus.COMPLETED, requestedBy, "Atendimento concluido", now);
    }

    public void cancel(String reason, String requestedBy) {
        if (!status.isOpenFlow()) {
            throw new DomainException("Attendance cannot be cancelled in current status");
        }
        LocalDateTime now = LocalDateTime.now();
        transitionTo(AttendanceStatus.CANCELLED, requestedBy, requireNonBlank(reason, "Cancel reason is required"), now);
    }

    private void transitionTo(AttendanceStatus nextStatus, String changedBy, String note, LocalDateTime when) {
        AttendanceStatus previous = this.status;
        this.status = nextStatus;
        this.updatedAt = when;
        addHistory(previous, nextStatus, changedBy, note, when);
    }

    private void addHistory(
        AttendanceStatus fromStatus,
        AttendanceStatus toStatus,
        String changedBy,
        String note,
        LocalDateTime changedAt
    ) {
        statusHistory.add(new AttendanceStatusChange(
            fromStatus,
            Objects.requireNonNull(toStatus, "toStatus cannot be null"),
            requireNonBlank(changedBy, "changedBy is required"),
            trimNullable(note),
            Objects.requireNonNull(changedAt, "changedAt cannot be null")
        ));
    }

    private static String trimNullable(String value) {
        return value == null ? null : value.trim();
    }

    private static String requireTicket(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Ticket number cannot be blank");
        }
        return value.trim();
    }

    private static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public UUID getNurseId() {
        return nurseId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public String getRoomLabel() {
        return roomLabel;
    }

    public String getCheckInNotes() {
        return checkInNotes;
    }

    public String getTriageNotes() {
        return triageNotes;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public String getOutcome() {
        return outcome;
    }

    public LocalDateTime getCheckInAt() {
        return checkInAt;
    }

    public LocalDateTime getTriageStartedAt() {
        return triageStartedAt;
    }

    public LocalDateTime getTriageFinishedAt() {
        return triageFinishedAt;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public LocalDateTime getConsultationStartedAt() {
        return consultationStartedAt;
    }

    public LocalDateTime getConsultationFinishedAt() {
        return consultationFinishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public List<AttendanceStatusChange> getStatusHistory() {
        return Collections.unmodifiableList(statusHistory);
    }
}
