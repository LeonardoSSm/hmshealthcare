package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.admission.AdmissionStatus;
import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdmitPatientUseCase {
    private final AdmissionRepository admissionRepository;
    private final BedRepository bedRepository;
    private final PatientRepository patientRepository;
    private final UserJpaRepository userJpaRepository;

    public AdmitPatientUseCase(
        AdmissionRepository admissionRepository,
        BedRepository bedRepository,
        PatientRepository patientRepository,
        UserJpaRepository userJpaRepository
    ) {
        this.admissionRepository = admissionRepository;
        this.bedRepository = bedRepository;
        this.patientRepository = patientRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Transactional
    public AdmissionResponse execute(AdmitPatientCommand command) {
        PatientId patientId = new PatientId(command.patientId());
        patientRepository.findById(patientId)
            .orElseThrow(() -> new DomainException("Patient not found"));

        boolean hasActiveAdmission = admissionRepository.findByPatientId(patientId).stream()
            .anyMatch(admission -> admission.getStatus() == AdmissionStatus.ACTIVE);
        if (hasActiveAdmission) {
            throw new DomainException("Patient already has an active admission");
        }

        UserEntity doctor = userJpaRepository.findById(command.doctorId().toString())
            .orElseThrow(() -> new DomainException("Doctor not found"));
        String role = normalizeRole(doctor.getRole());
        if (!doctor.isActive() || !"DOCTOR".equals(role)) {
            throw new DomainException("Responsible professional must be an active doctor");
        }

        Bed bed = bedRepository.findByIdForUpdate(command.bedId())
            .orElseThrow(() -> new DomainException("Bed not found"));
        bed.occupy();
        bedRepository.save(bed);

        Admission admission = Admission.admit(
            patientId,
            command.bedId(),
            command.doctorId(),
            command.reason()
        );

        Admission saved = admissionRepository.save(admission);
        return AdmissionResponse.from(saved);
    }

    private static String normalizeRole(String rawRole) {
        String normalized = rawRole == null ? "" : rawRole.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized.substring(5) : normalized;
    }
}
