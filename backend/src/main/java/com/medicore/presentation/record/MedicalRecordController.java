package com.medicore.presentation.record;

import com.medicore.application.record.AddDiagnosisUseCase;
import com.medicore.application.record.AddMedicalRecordEventUseCase;
import com.medicore.application.record.DiagnosisRequest;
import com.medicore.application.record.GetMedicalRecordByPatientUseCase;
import com.medicore.application.record.MedicalRecordResponse;
import com.medicore.application.record.MedicalRecordEventRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
    private final GetMedicalRecordByPatientUseCase getMedicalRecordByPatientUseCase;
    private final AddDiagnosisUseCase addDiagnosisUseCase;
    private final AddMedicalRecordEventUseCase addMedicalRecordEventUseCase;

    public MedicalRecordController(
        GetMedicalRecordByPatientUseCase getMedicalRecordByPatientUseCase,
        AddDiagnosisUseCase addDiagnosisUseCase,
        AddMedicalRecordEventUseCase addMedicalRecordEventUseCase
    ) {
        this.getMedicalRecordByPatientUseCase = getMedicalRecordByPatientUseCase;
        this.addDiagnosisUseCase = addDiagnosisUseCase;
        this.addMedicalRecordEventUseCase = addMedicalRecordEventUseCase;
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public MedicalRecordResponse getByPatient(@PathVariable UUID patientId) {
        return getMedicalRecordByPatientUseCase.execute(patientId);
    }

    @PostMapping("/{medicalRecordId}/diagnoses")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public MedicalRecordResponse addDiagnosis(@PathVariable UUID medicalRecordId, @Valid @RequestBody DiagnosisRequest request) {
        return addDiagnosisUseCase.execute(medicalRecordId, request);
    }

    @PostMapping("/{medicalRecordId}/events")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public MedicalRecordResponse addEvent(@PathVariable UUID medicalRecordId, @Valid @RequestBody MedicalRecordEventRequest request) {
        return addMedicalRecordEventUseCase.execute(medicalRecordId, request);
    }
}
