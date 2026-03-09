package com.medicore.domain.record;

import com.medicore.domain.patient.PatientId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MedicalRecord {
    private final UUID id;
    private final PatientId patientId;
    private final LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private String observations;
    private final List<Diagnosis> diagnoses;
    private final List<Prescription> prescriptions;
    private final List<MedicalRecordEvent> events;

    private MedicalRecord(
        UUID id,
        PatientId patientId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String observations,
        List<Diagnosis> diagnoses,
        List<Prescription> prescriptions,
        List<MedicalRecordEvent> events
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.patientId = Objects.requireNonNull(patientId, "patientId cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
        this.observations = observations == null ? "" : observations;
        this.diagnoses = new ArrayList<>(diagnoses == null ? List.of() : diagnoses);
        this.prescriptions = new ArrayList<>(prescriptions == null ? List.of() : prescriptions);
        this.events = new ArrayList<>(events == null ? List.of() : events);
    }

    public static MedicalRecord createFor(PatientId patientId) {
        LocalDateTime now = LocalDateTime.now();
        return new MedicalRecord(UUID.randomUUID(), patientId, now, now, "", List.of(), List.of(), List.of());
    }

    public static MedicalRecord rehydrate(
        UUID id,
        PatientId patientId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String observations,
        List<Diagnosis> diagnoses,
        List<Prescription> prescriptions,
        List<MedicalRecordEvent> events
    ) {
        return new MedicalRecord(id, patientId, createdAt, updatedAt, observations, diagnoses, prescriptions, events);
    }

    public void addDiagnosis(Diagnosis diagnosis) {
        diagnoses.add(Objects.requireNonNull(diagnosis, "diagnosis cannot be null"));
        this.updatedAt = LocalDateTime.now();
    }

    public void addPrescription(Prescription prescription) {
        prescriptions.add(Objects.requireNonNull(prescription, "prescription cannot be null"));
        this.updatedAt = LocalDateTime.now();
    }

    public void addEvent(MedicalRecordEvent event) {
        events.add(Objects.requireNonNull(event, "event cannot be null"));
        this.updatedAt = LocalDateTime.now();
    }

    public void updateObservations(String observations) {
        this.observations = observations == null ? "" : observations.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getObservations() {
        return observations;
    }

    public List<Diagnosis> getDiagnoses() {
        return Collections.unmodifiableList(diagnoses);
    }

    public List<Prescription> getPrescriptions() {
        return Collections.unmodifiableList(prescriptions);
    }

    public List<MedicalRecordEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
