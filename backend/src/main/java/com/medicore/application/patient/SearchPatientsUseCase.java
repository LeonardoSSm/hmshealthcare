package com.medicore.application.patient;

import com.medicore.domain.patient.PatientRepository;
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
}
