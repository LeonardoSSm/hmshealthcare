package com.medicore.infrastructure.persistence.attendance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attendances")
public class AttendanceEntity {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "ticket_number", nullable = false, length = 20, unique = true)
    private String ticketNumber;

    @Column(name = "patient_id", nullable = false, length = 36)
    private String patientId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "priority_score", nullable = false)
    private int priorityScore;

    @Column(name = "nurse_id", length = 36)
    private String nurseId;

    @Column(name = "doctor_id", length = 36)
    private String doctorId;

    @Column(name = "room_label", length = 40)
    private String roomLabel;

    @Column(name = "check_in_notes", length = 500)
    private String checkInNotes;

    @Column(name = "triage_notes")
    private String triageNotes;

    @Column(name = "consultation_notes")
    private String consultationNotes;

    @Column(name = "outcome")
    private String outcome;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @Column(name = "triage_started_at")
    private LocalDateTime triageStartedAt;

    @Column(name = "triage_finished_at")
    private LocalDateTime triageFinishedAt;

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "consultation_started_at")
    private LocalDateTime consultationStartedAt;

    @Column(name = "consultation_finished_at")
    private LocalDateTime consultationFinishedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttendanceStatusHistoryEntity> statusHistory = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getNurseId() {
        return nurseId;
    }

    public void setNurseId(String nurseId) {
        this.nurseId = nurseId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getRoomLabel() {
        return roomLabel;
    }

    public void setRoomLabel(String roomLabel) {
        this.roomLabel = roomLabel;
    }

    public String getCheckInNotes() {
        return checkInNotes;
    }

    public void setCheckInNotes(String checkInNotes) {
        this.checkInNotes = checkInNotes;
    }

    public String getTriageNotes() {
        return triageNotes;
    }

    public void setTriageNotes(String triageNotes) {
        this.triageNotes = triageNotes;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public LocalDateTime getCheckInAt() {
        return checkInAt;
    }

    public void setCheckInAt(LocalDateTime checkInAt) {
        this.checkInAt = checkInAt;
    }

    public LocalDateTime getTriageStartedAt() {
        return triageStartedAt;
    }

    public void setTriageStartedAt(LocalDateTime triageStartedAt) {
        this.triageStartedAt = triageStartedAt;
    }

    public LocalDateTime getTriageFinishedAt() {
        return triageFinishedAt;
    }

    public void setTriageFinishedAt(LocalDateTime triageFinishedAt) {
        this.triageFinishedAt = triageFinishedAt;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }

    public LocalDateTime getConsultationStartedAt() {
        return consultationStartedAt;
    }

    public void setConsultationStartedAt(LocalDateTime consultationStartedAt) {
        this.consultationStartedAt = consultationStartedAt;
    }

    public LocalDateTime getConsultationFinishedAt() {
        return consultationFinishedAt;
    }

    public void setConsultationFinishedAt(LocalDateTime consultationFinishedAt) {
        this.consultationFinishedAt = consultationFinishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public List<AttendanceStatusHistoryEntity> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<AttendanceStatusHistoryEntity> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
