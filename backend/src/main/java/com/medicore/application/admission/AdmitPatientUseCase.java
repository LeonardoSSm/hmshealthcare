package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdmitPatientUseCase {
    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;

    public AdmitPatientUseCase(AdmissionRepository admissionRepository, PatientRepository patientRepository) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public AdmissionResponse execute(AdmitPatientCommand command) {
        patientRepository.findById(new PatientId(command.patientId()))
            .orElseThrow(() -> new DomainException("Patient not found"));

        Admission admission = Admission.admit(
            new PatientId(command.patientId()),
            command.bedId(),
            command.doctorId(),
            command.reason()
        );

        Admission saved = admissionRepository.save(admission);
        return AdmissionResponse.from(saved);
    }
}
