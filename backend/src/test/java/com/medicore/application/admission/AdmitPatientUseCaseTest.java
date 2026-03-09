package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.admission.BedStatus;
import com.medicore.domain.admission.BedType;
import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdmitPatientUseCaseTest {
    @Mock
    private AdmissionRepository admissionRepository;

    @Mock
    private BedRepository bedRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private AdmitPatientUseCase useCase;

    @Test
    void shouldRejectWhenPatientAlreadyHasActiveAdmission() {
        UUID patientId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(org.mockito.Mockito.mock(Patient.class)));
        when(admissionRepository.findByPatientId(any(PatientId.class)))
            .thenReturn(List.of(Admission.admit(new PatientId(patientId), bedId, doctorId, "Teste")));

        DomainException error = assertThrows(DomainException.class, () -> useCase.execute(
            new AdmitPatientCommand(patientId, bedId, doctorId, "Dor toracica")
        ));

        assertEquals("Patient already has an active admission", error.getMessage());
    }

    @Test
    void shouldRejectWhenResponsibleProfessionalIsNotDoctor() {
        UUID patientId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UserEntity nurse = new UserEntity();
        nurse.setId(doctorId.toString());
        nurse.setActive(true);
        nurse.setRole("NURSE");

        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(org.mockito.Mockito.mock(Patient.class)));
        when(admissionRepository.findByPatientId(any(PatientId.class))).thenReturn(List.of());
        when(userJpaRepository.findById(doctorId.toString())).thenReturn(Optional.of(nurse));

        DomainException error = assertThrows(DomainException.class, () -> useCase.execute(
            new AdmitPatientCommand(patientId, bedId, doctorId, "Insuficiencia respiratoria")
        ));

        assertEquals("Responsible professional must be an active doctor", error.getMessage());
    }

    @Test
    void shouldOccupyBedAndSaveAdmissionWhenPayloadIsValid() {
        UUID patientId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        Bed availableBed = new Bed(bedId, "UTI-02", 2, "UTI", BedType.ICU, BedStatus.AVAILABLE);
        UserEntity doctor = new UserEntity();
        doctor.setId(doctorId.toString());
        doctor.setActive(true);
        doctor.setRole("DOCTOR");

        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(org.mockito.Mockito.mock(Patient.class)));
        when(admissionRepository.findByPatientId(any(PatientId.class))).thenReturn(List.of());
        when(userJpaRepository.findById(doctorId.toString())).thenReturn(Optional.of(doctor));
        when(bedRepository.findByIdForUpdate(bedId)).thenReturn(Optional.of(availableBed));
        when(admissionRepository.save(any(Admission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdmissionResponse response = useCase.execute(
            new AdmitPatientCommand(patientId, bedId, doctorId, "Queda de saturacao")
        );

        assertEquals("ACTIVE", response.status());
        verify(bedRepository).save(any(Bed.class));
        verify(admissionRepository).save(any(Admission.class));
    }
}
