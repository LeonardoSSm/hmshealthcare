package com.medicore.application.admission;

import com.medicore.domain.admission.Admission;
import com.medicore.domain.admission.AdmissionRepository;
import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.admission.BedStatus;
import com.medicore.domain.admission.BedType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DischargePatientUseCaseTest {
    @Mock
    private AdmissionRepository admissionRepository;

    @Mock
    private BedRepository bedRepository;

    @InjectMocks
    private DischargePatientUseCase useCase;

    @Test
    void shouldReleaseBedAndMarkAdmissionAsDischarged() {
        UUID admissionId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID bedId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Admission admission = Admission.admit(new com.medicore.domain.patient.PatientId(patientId), bedId, doctorId, "Monitoracao");
        Bed occupiedBed = new Bed(bedId, "ENF-01", 3, "Enfermaria", BedType.WARD, BedStatus.OCCUPIED);

        when(admissionRepository.findById(admissionId)).thenReturn(Optional.of(admission));
        when(bedRepository.findByIdForUpdate(bedId)).thenReturn(Optional.of(occupiedBed));
        when(admissionRepository.save(any(Admission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdmissionResponse response = useCase.execute(admissionId);

        assertEquals("DISCHARGED", response.status());
        verify(bedRepository).save(any(Bed.class));
        verify(admissionRepository).save(any(Admission.class));
    }
}
