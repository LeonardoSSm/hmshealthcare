package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DischargePatientUseCase {
    private final AdmissionRepository admissionRepository;
    private final BedRepository bedRepository;

    public DischargePatientUseCase(AdmissionRepository admissionRepository, BedRepository bedRepository) {
        this.admissionRepository = admissionRepository;
        this.bedRepository = bedRepository;
    }

    @Transactional
    public AdmissionResponse execute(UUID admissionId) {
        Admission admission = admissionRepository.findById(admissionId)
            .orElseThrow(() -> new DomainException("Admission not found"));
        admission.discharge();

        Bed bed = bedRepository.findByIdForUpdate(admission.getBedId())
            .orElseThrow(() -> new DomainException("Bed not found"));
        bed.markAvailable();
        bedRepository.save(bed);

        Admission saved = admissionRepository.save(admission);
        return AdmissionResponse.from(saved);
    }
}
