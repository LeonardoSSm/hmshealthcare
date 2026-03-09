package com.medicore.infrastructure.persistence.record;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_record_events")
@Audited
public class MedicalRecordEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "author", nullable = false, length = 120)
    private String author;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "notes")
    private String notes;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    public Long getId() { return id; }
    public MedicalRecordEntity getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecordEntity medicalRecord) { this.medicalRecord = medicalRecord; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
