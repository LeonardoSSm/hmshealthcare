package com.medicore.presentation.patient;

import com.medicore.application.patient.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final RegisterPatientUseCase registerPatientUseCase;
    private final GetPatientUseCase getPatientUseCase;
    private final SearchPatientsUseCase searchPatientsUseCase;
    private final DeactivatePatientUseCase deactivatePatientUseCase;

    public PatientController(
        RegisterPatientUseCase registerPatientUseCase,
        GetPatientUseCase getPatientUseCase,
        SearchPatientsUseCase searchPatientsUseCase,
        DeactivatePatientUseCase deactivatePatientUseCase
    ) {
        this.registerPatientUseCase = registerPatientUseCase;
        this.getPatientUseCase = getPatientUseCase;
        this.searchPatientsUseCase = searchPatientsUseCase;
        this.deactivatePatientUseCase = deactivatePatientUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        RegisterPatientCommand command = new RegisterPatientCommand(
            request.name(),
            request.cpf(),
            request.birthDate(),
            request.bloodType(),
            request.allergies(),
            request.phone(),
            request.email(),
            request.address()
        );
        return registerPatientUseCase.execute(command);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public PatientResponse getById(@PathVariable UUID id) {
        return getPatientUseCase.execute(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<PatientResponse> search(@RequestParam(required = false, defaultValue = "") String query) {
        return searchPatientsUseCase.execute(query);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public void deactivate(@PathVariable UUID id) {
        deactivatePatientUseCase.execute(id);
    }
}
