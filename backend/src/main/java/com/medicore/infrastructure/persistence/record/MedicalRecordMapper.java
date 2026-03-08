package com.medicore.infrastructure.persistence.record;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.record.Diagnosis;
import com.medicore.domain.record.MedicalRecord;
import com.medicore.domain.record.Prescription;
import com.medicore.domain.shared.DateRange;

import java.util.List;
import java.util.UUID;

public final class MedicalRecordMapper {
    private MedicalRecordMapper() {
    }

    public static MedicalRecordEntity toEntity(MedicalRecord record) {
        MedicalRecordEntity entity = new MedicalRecordEntity();
        entity.setId(record.getId().toString());
        entity.setPatientId(record.getPatientId().value().toString());
        entity.setObservations(record.getObservations());
        entity.setCreatedAt(record.getCreatedAt());
        entity.setUpdatedAt(record.getUpdatedAt());

        List<DiagnosisEntity> diagnoses = record.getDiagnoses().stream().map(d -> {
            DiagnosisEntity de = new DiagnosisEntity();
            de.setMedicalRecord(entity);
            de.setDoctorId(d.doctorId().toString());
            de.setIcd10Code(d.icd10Code());
            de.setDescription(d.description());
            de.setNotes(d.notes());
            de.setDate(d.diagnosedAt());
            return de;
        }).toList();

        List<PrescriptionEntity> prescriptions = record.getPrescriptions().stream().map(p -> {
            PrescriptionEntity pe = new PrescriptionEntity();
            pe.setMedicalRecord(entity);
            pe.setDoctorId(p.doctorId().toString());
            pe.setMedication(p.medication());
            pe.setDosage(p.dosage());
            pe.setFrequency(p.frequency());
            pe.setStartDate(p.period().start());
            pe.setEndDate(p.period().end());
            return pe;
        }).toList();

        entity.setDiagnoses(diagnoses);
        entity.setPrescriptions(prescriptions);
        return entity;
    }

    public static MedicalRecord toDomain(MedicalRecordEntity entity) {
        List<Diagnosis> diagnoses = entity.getDiagnoses().stream().map(d -> new Diagnosis(
            UUID.fromString(d.getDoctorId()), d.getIcd10Code(), d.getDescription(), d.getNotes(), d.getDate()
        )).toList();

        List<Prescription> prescriptions = entity.getPrescriptions().stream().map(p -> new Prescription(
            UUID.fromString(p.getDoctorId()),
            p.getMedication(),
            p.getDosage(),
            p.getFrequency(),
            new DateRange(p.getStartDate(), p.getEndDate())
        )).toList();

        return MedicalRecord.rehydrate(
            UUID.fromString(entity.getId()),
            new PatientId(UUID.fromString(entity.getPatientId())),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getObservations(),
            diagnoses,
            prescriptions
        );
    }
}
