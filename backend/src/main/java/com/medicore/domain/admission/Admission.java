package com.medicore.domain.admission;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.shared.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Admission {
    private final UUID id;
    private final PatientId patientId;
    private final UUID bedId;
    private final UUID doctorId;
    private final LocalDateTime admissionDate;
    private final String reason;

    private LocalDateTime dischargeDate;
    private AdmissionStatus status;

    private Admission(
        UUID id,
        PatientId patientId,
        UUID bedId,
        UUID doctorId,
        LocalDateTime admissionDate,
        String reason,
        LocalDateTime dischargeDate,
        AdmissionStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.patientId = Objects.requireNonNull(patientId, "patientId cannot be null");
        this.bedId = Objects.requireNonNull(bedId, "bedId cannot be null");
        this.doctorId = Objects.requireNonNull(doctorId, "doctorId cannot be null");
        this.admissionDate = Objects.requireNonNull(admissionDate, "admissionDate cannot be null");
        if (reason == null || reason.isBlank()) {
            throw new DomainException("Admission reason cannot be blank");
        }
        this.reason = reason.trim();
        this.dischargeDate = dischargeDate;
        this.status = Objects.requireNonNull(status, "status cannot be null");
    }

    public static Admission admit(PatientId patientId, UUID bedId, UUID doctorId, String reason) {
        return new Admission(
            UUID.randomUUID(),
            patientId,
            bedId,
            doctorId,
            LocalDateTime.now(),
            reason,
            null,
            AdmissionStatus.ACTIVE
        );
    }

    public static Admission rehydrate(
        UUID id,
        PatientId patientId,
        UUID bedId,
        UUID doctorId,
        LocalDateTime admissionDate,
        String reason,
        LocalDateTime dischargeDate,
        AdmissionStatus status
    ) {
        return new Admission(id, patientId, bedId, doctorId, admissionDate, reason, dischargeDate, status);
    }

    public void discharge() {
        if (status == AdmissionStatus.DISCHARGED) {
            throw new DomainException("Admission already discharged");
        }
        this.status = AdmissionStatus.DISCHARGED;
        this.dischargeDate = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public UUID getBedId() {
        return bedId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public String getReason() {
        return reason;
    }

    public AdmissionStatus getStatus() {
        return status;
    }
}
