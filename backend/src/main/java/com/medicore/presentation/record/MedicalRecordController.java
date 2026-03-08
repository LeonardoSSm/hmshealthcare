package com.medicore.presentation.record;

import com.medicore.application.record.AddDiagnosisUseCase;
import com.medicore.application.record.DiagnosisRequest;
import com.medicore.application.record.GetMedicalRecordByPatientUseCase;
import com.medicore.application.record.MedicalRecordResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
    private final GetMedicalRecordByPatientUseCase getMedicalRecordByPatientUseCase;
    private final AddDiagnosisUseCase addDiagnosisUseCase;

    public MedicalRecordController(
        GetMedicalRecordByPatientUseCase getMedicalRecordByPatientUseCase,
        AddDiagnosisUseCase addDiagnosisUseCase
    ) {
        this.getMedicalRecordByPatientUseCase = getMedicalRecordByPatientUseCase;
        this.addDiagnosisUseCase = addDiagnosisUseCase;
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public MedicalRecordResponse getByPatient(@PathVariable UUID patientId) {
        return getMedicalRecordByPatientUseCase.execute(patientId);
    }

    @PostMapping("/{medicalRecordId}/diagnoses")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public MedicalRecordResponse addDiagnosis(@PathVariable UUID medicalRecordId, @RequestBody DiagnosisRequest request) {
        return addDiagnosisUseCase.execute(medicalRecordId, request);
    }
}
