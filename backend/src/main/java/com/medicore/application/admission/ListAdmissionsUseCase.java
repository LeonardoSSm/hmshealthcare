package com.medicore.application.admission;

import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.shared.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListAdmissionsUseCase {
    private final AdmissionRepository admissionRepository;

    public ListAdmissionsUseCase(AdmissionRepository admissionRepository) {
        this.admissionRepository = admissionRepository;
    }

    @Transactional(readOnly = true)
    public PagedResult<AdmissionResponse> execute(int page, int size) {
        PagedResult<com.medicore.domain.admission.Admission> result = admissionRepository.findAllPaged(page, size);
        return new PagedResult<>(
            result.content().stream().map(AdmissionResponse::from).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()
        );
    }
}
