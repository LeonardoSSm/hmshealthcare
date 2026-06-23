package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.attendance.AttendanceStatus;
import com.medicore.domain.patient.Patient;
import com.medicore.domain.patient.PatientId;
import com.medicore.domain.patient.PatientRepository;
import com.medicore.domain.shared.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInAttendanceUseCaseTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AttendanceTicketGenerator ticketGenerator;
    @Mock
    private AttendanceResponseMapper responseMapper;

    @InjectMocks
    private CheckInAttendanceUseCase useCase;

    @Test
    void shouldThrow_whenPatientNotFound() {
        UUID patientId = UUID.randomUUID();
        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () ->
            useCase.execute(new CheckInAttendanceCommand(patientId, null, null))
        );
        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    void shouldThrow_whenPatientIsInactive() {
        Patient inactivePatient = mock(Patient.class);
        when(inactivePatient.isActive()).thenReturn(false);
        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(inactivePatient));

        DomainException ex = assertThrows(DomainException.class, () ->
            useCase.execute(new CheckInAttendanceCommand(UUID.randomUUID(), null, null))
        );
        assertEquals("Inactive patient cannot be checked in", ex.getMessage());
    }

    @Test
    void shouldThrow_whenPatientAlreadyHasActiveAttendance() {
        Patient activePatient = mock(Patient.class);
        when(activePatient.isActive()).thenReturn(true);
        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
        when(attendanceRepository.existsOpenByPatientId(any(PatientId.class))).thenReturn(true);

        DomainException ex = assertThrows(DomainException.class, () ->
            useCase.execute(new CheckInAttendanceCommand(UUID.randomUUID(), null, null))
        );
        assertEquals("Patient already has an active attendance flow", ex.getMessage());
    }

    @Test
    void shouldCheckIn_whenPayloadIsValid() {
        UUID patientId = UUID.randomUUID();
        Patient activePatient = mock(Patient.class);
        when(activePatient.isActive()).thenReturn(true);
        when(patientRepository.findById(any(PatientId.class))).thenReturn(Optional.of(activePatient));
        when(attendanceRepository.existsOpenByPatientId(any(PatientId.class))).thenReturn(false);
        when(ticketGenerator.generate()).thenReturn("A001");
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> inv.getArgument(0));

        AttendanceResponse fakeResponse = mock(AttendanceResponse.class);
        when(responseMapper.toResponse(any(Attendance.class))).thenReturn(fakeResponse);

        AttendanceResponse result = useCase.execute(new CheckInAttendanceCommand(patientId, "dor", "Recepcao"));

        assertSame(fakeResponse, result);
        verify(attendanceRepository).save(argThat(a -> a.getStatus() == AttendanceStatus.WAITING_TRIAGE));
    }
}
