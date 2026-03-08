package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DischargePatientUseCase {
    private final AdmissionRepository admissionRepository;

    public DischargePatientUseCase(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    @Transactional
    public AdmissionResponse execute(UUID admissionId) {
        Admission admission = admissionRepository.findById(admissionId)
            .orElseThrow(() -> new DomainException("Admission not found"));
        admission.discharge();
        Admission saved = admissionRepository.save(admission);
        return AdmissionResponse.from(saved);
    }
}
