package com.medicore.infrastructure.persistence.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionStatus;
import com.medicore.domain.patient.PatientId;

import java.util.UUID;

public final class AdmissionMapper {
    private AdmissionMapper() {
    }

    public static AdmissionEntity toEntity(Admission admission) {
        AdmissionEntity entity = new AdmissionEntity();
        entity.setId(admission.getId().toString());
        entity.setPatientId(admission.getPatientId().value().toString());
        entity.setBedId(admission.getBedId().toString());
        entity.setDoctorId(admission.getDoctorId().toString());
        entity.setAdmissionDate(admission.getAdmissionDate());
        entity.setDischargeDate(admission.getDischargeDate());
        entity.setReason(admission.getReason());
        entity.setStatus(admission.getStatus().name());
        return entity;
    }

    public static Admission toDomain(AdmissionEntity entity) {
        return Admission.rehydrate(
            UUID.fromString(entity.getId()),
            new PatientId(UUID.fromString(entity.getPatientId())),
            UUID.fromString(entity.getBedId()),
            UUID.fromString(entity.getDoctorId()),
            entity.getAdmissionDate(),
            entity.getReason(),
            entity.getDischargeDate(),
            AdmissionStatus.valueOf(entity.getStatus())
        );
    }
}
