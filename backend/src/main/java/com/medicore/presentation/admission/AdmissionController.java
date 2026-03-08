package com.medicore.presentation.admission;

import com.medicore.application.admission.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {
    private final AdmitPatientUseCase admitPatientUseCase;
    private final DischargePatientUseCase dischargePatientUseCase;
    private final ListAdmissionsByPatientUseCase listAdmissionsByPatientUseCase;

    public AdmissionController(
        AdmitPatientUseCase admitPatientUseCase,
        DischargePatientUseCase dischargePatientUseCase,
        ListAdmissionsByPatientUseCase listAdmissionsByPatientUseCase
    ) {
        this.admitPatientUseCase = admitPatientUseCase;
        this.dischargePatientUseCase = dischargePatientUseCase;
        this.listAdmissionsByPatientUseCase = listAdmissionsByPatientUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public AdmissionResponse admit(@RequestBody AdmitPatientCommand command) {
        return admitPatientUseCase.execute(command);
    }

    @PatchMapping("/{admissionId}/discharge")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public AdmissionResponse discharge(@PathVariable UUID admissionId) {
        return dischargePatientUseCase.execute(admissionId);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<AdmissionResponse> byPatient(@PathVariable UUID patientId) {
        return listAdmissionsByPatientUseCase.execute(patientId);
    }
}
