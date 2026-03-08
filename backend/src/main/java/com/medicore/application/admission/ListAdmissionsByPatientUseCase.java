package com.medicore.application.admission;

import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.patient.PatientId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListAdmissionsByPatientUseCase {
    private final AdmissionRepository admissionRepository;

    public ListAdmissionsByPatientUseCase(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    public List<AdmissionResponse> execute(UUID patientId) {
        return admissionRepository.findByPatientId(new PatientId(patientId)).stream()
            .map(AdmissionResponse::from)
            .toList();
    }
}
