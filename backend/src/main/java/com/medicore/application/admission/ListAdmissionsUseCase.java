package com.medicore.application.admission;

import com.medicore.domain.admission.AdmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAdmissionsUseCase {
    private final AdmissionRepository admissionRepository;

    public ListAdmissionsUseCase(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponse> execute() {
        return admissionRepository.findAll().stream()
            .map(AdmissionResponse::from)
            .toList();
    }
}
