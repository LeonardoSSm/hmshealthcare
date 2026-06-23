package com.medicore.application.patient;

import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.PagedResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchPatientsUseCase {
    private final PatientRepository patientRepository;

    public SearchPatientsUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponse> execute(String query) {
        return patientRepository.search(query).stream().map(PatientResponseMapper::toResponse).toList();
    }

    public PagedResult<PatientResponse> executePaged(String query, int page, int size) {
        PagedResult<com.medicore.domain.patient.Patient> result = patientRepository.searchPaged(query, page, size);
        return new PagedResult<>(
            result.content().stream().map(PatientResponseMapper::toResponse).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()
        );
    }
}
