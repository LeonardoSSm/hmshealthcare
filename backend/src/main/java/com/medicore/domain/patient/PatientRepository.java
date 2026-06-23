package com.medicore.domain.patient;

import com.medicore.domain.shared.CPF;
import com.medicore.domain.shared.PagedResult;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(PatientId id);
    Optional<Patient> findByCpf(CPF cpf);
    List<Patient> search(String query);
    PagedResult<Patient> searchPaged(String query, int page, int size);
}
