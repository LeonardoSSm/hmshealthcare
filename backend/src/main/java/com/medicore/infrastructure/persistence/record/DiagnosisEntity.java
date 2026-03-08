package com.medicore.infrastructure.persistence.record;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnoses")
@Audited
public class DiagnosisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecordEntity medicalRecord;

    @Column(name = "doctor_id", nullable = false, length = 36)
    private String doctorId;

    @Column(name = "icd10_code", nullable = false, length = 16)
    private String icd10Code;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "notes")
    private String notes;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public Long getId() { return id; }
    public MedicalRecordEntity getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecordEntity medicalRecord) { this.medicalRecord = medicalRecord; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getIcd10Code() { return icd10Code; }
    public void setIcd10Code(String icd10Code) { this.icd10Code = icd10Code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
