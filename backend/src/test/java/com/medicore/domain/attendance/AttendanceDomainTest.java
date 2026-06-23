package com.medicore.domain.attendance;

import com.medicore.domain.patient.PatientId;
import com.medicore.domain.shared.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceDomainTest {

    private static final PatientId PATIENT_ID = new PatientId(UUID.randomUUID());
    private static final UUID NURSE_ID = UUID.randomUUID();
    private static final UUID DOCTOR_ID = UUID.randomUUID();

    // --- checkIn ---

    @Test
    void checkIn_shouldCreateAttendanceInWaitingTriageStatus() {
        Attendance attendance = Attendance.checkIn(PATIENT_ID, "A001", "dor de cabeca", "Recepcao");

        assertEquals(AttendanceStatus.WAITING_TRIAGE, attendance.getStatus());
        assertEquals("A001", attendance.getTicketNumber());
        assertEquals(PATIENT_ID, attendance.getPatientId());
        assertEquals(1, attendance.getStatusHistory().size());
    }

    @Test
    void checkIn_shouldRejectBlankTicketNumber() {
        assertThrows(DomainException.class, () ->
            Attendance.checkIn(PATIENT_ID, "  ", null, "Recepcao")
        );
    }

    // --- startTriage ---

    @Test
    void startTriage_shouldTransitionToInTriage() {
        Attendance attendance = checkInAttendance();

        attendance.startTriage(NURSE_ID, "Enfermeira Ana");

        assertEquals(AttendanceStatus.IN_TRIAGE, attendance.getStatus());
        assertEquals(NURSE_ID, attendance.getNurseId());
        assertNotNull(attendance.getTriageStartedAt());
        assertEquals(2, attendance.getStatusHistory().size());
    }

    @Test
    void startTriage_shouldFail_whenNotInWaitingTriage() {
        Attendance attendance = checkInAttendance();
        attendance.startTriage(NURSE_ID, "Enfermeira Ana");

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.startTriage(NURSE_ID, "Enfermeira Ana")
        );
        assertEquals("Attendance is not waiting for triage", ex.getMessage());
    }

    // --- finishTriage ---

    @Test
    void finishTriage_shouldTransitionToWaitingDoctor_withRiskLevelAndPriorityScore() {
        Attendance attendance = checkInAttendance();
        attendance.startTriage(NURSE_ID, "Enfermeira Ana");

        attendance.finishTriage(NURSE_ID, RiskLevel.YELLOW, "paciente estavel", "Enfermeira Ana");

        assertEquals(AttendanceStatus.WAITING_DOCTOR, attendance.getStatus());
        assertEquals(RiskLevel.YELLOW, attendance.getRiskLevel());
        assertEquals(RiskLevel.YELLOW.priorityScore(), attendance.getPriorityScore());
        assertNotNull(attendance.getTriageFinishedAt());
    }

    @Test
    void finishTriage_shouldFail_whenNotInTriage() {
        Attendance attendance = checkInAttendance();

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.finishTriage(NURSE_ID, RiskLevel.GREEN, null, "Enfermeira Ana")
        );
        assertEquals("Attendance is not in triage", ex.getMessage());
    }

    @Test
    void finishTriage_shouldFail_whenDifferentNurseTriesToFinish() {
        Attendance attendance = checkInAttendance();
        attendance.startTriage(NURSE_ID, "Enfermeira Ana");
        UUID otherNurse = UUID.randomUUID();

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.finishTriage(otherNurse, RiskLevel.GREEN, null, "Enfermeira B")
        );
        assertEquals("Only the responsible nurse can finish triage", ex.getMessage());
    }

    // --- callDoctor ---

    @Test
    void callDoctor_shouldTransitionToCalledDoctor() {
        Attendance attendance = advancedToWaitingDoctor();

        attendance.callDoctor(DOCTOR_ID, "Sala 3", "Recepcao");

        assertEquals(AttendanceStatus.CALLED_DOCTOR, attendance.getStatus());
        assertEquals(DOCTOR_ID, attendance.getDoctorId());
        assertEquals("Sala 3", attendance.getRoomLabel());
        assertNotNull(attendance.getCalledAt());
    }

    @Test
    void callDoctor_shouldFail_whenRoomLabelIsBlank() {
        Attendance attendance = advancedToWaitingDoctor();

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.callDoctor(DOCTOR_ID, "  ", "Recepcao")
        );
        assertEquals("Room label is required", ex.getMessage());
    }

    @Test
    void callDoctor_shouldFail_whenNotWaitingDoctor() {
        Attendance attendance = checkInAttendance();

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.callDoctor(DOCTOR_ID, "Sala 1", "Recepcao")
        );
        assertEquals("Attendance is not waiting for doctor", ex.getMessage());
    }

    // --- startConsultation ---

    @Test
    void startConsultation_shouldTransitionToInConsultation() {
        Attendance attendance = advancedToCalledDoctor();

        attendance.startConsultation(DOCTOR_ID, "Dr. Carlos");

        assertEquals(AttendanceStatus.IN_CONSULTATION, attendance.getStatus());
        assertNotNull(attendance.getConsultationStartedAt());
    }

    @Test
    void startConsultation_shouldFail_whenDifferentDoctorTries() {
        Attendance attendance = advancedToCalledDoctor();
        UUID otherDoctor = UUID.randomUUID();

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.startConsultation(otherDoctor, "Dr. X")
        );
        assertEquals("Only the called doctor can start consultation", ex.getMessage());
    }

    @Test
    void startConsultation_shouldFail_whenNotCalledDoctor() {
        Attendance attendance = advancedToWaitingDoctor();

        assertThrows(DomainException.class, () ->
            attendance.startConsultation(DOCTOR_ID, "Dr. Carlos")
        );
    }

    // --- finishConsultation ---

    @Test
    void finishConsultation_shouldTransitionToCompleted() {
        Attendance attendance = advancedToInConsultation();

        attendance.finishConsultation(DOCTOR_ID, "Alta medica", "Paciente estavel", "Dr. Carlos");

        assertEquals(AttendanceStatus.COMPLETED, attendance.getStatus());
        assertEquals("Alta medica", attendance.getOutcome());
        assertNotNull(attendance.getConsultationFinishedAt());
    }

    @Test
    void finishConsultation_shouldFail_whenOutcomeIsBlank() {
        Attendance attendance = advancedToInConsultation();

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.finishConsultation(DOCTOR_ID, "  ", null, "Dr. Carlos")
        );
        assertEquals("Outcome is required", ex.getMessage());
    }

    @Test
    void finishConsultation_shouldFail_whenNotInConsultation() {
        Attendance attendance = advancedToWaitingDoctor();

        assertThrows(DomainException.class, () ->
            attendance.finishConsultation(DOCTOR_ID, "Alta", null, "Dr. Carlos")
        );
    }

    // --- cancel ---

    @Test
    void cancel_shouldWork_whenInWaitingTriage() {
        Attendance attendance = checkInAttendance();

        attendance.cancel("Paciente saiu", "Recepcao");

        assertEquals(AttendanceStatus.CANCELLED, attendance.getStatus());
    }

    @Test
    void cancel_shouldWork_whenInTriage() {
        Attendance attendance = checkInAttendance();
        attendance.startTriage(NURSE_ID, "Enfermeira Ana");

        attendance.cancel("Desistencia", "Enfermagem");

        assertEquals(AttendanceStatus.CANCELLED, attendance.getStatus());
    }

    @Test
    void cancel_shouldWork_whenWaitingDoctor() {
        Attendance attendance = advancedToWaitingDoctor();

        attendance.cancel("Transferido", "Medico");

        assertEquals(AttendanceStatus.CANCELLED, attendance.getStatus());
    }

    @Test
    void cancel_shouldFail_whenAlreadyCompleted() {
        Attendance attendance = advancedToInConsultation();
        attendance.finishConsultation(DOCTOR_ID, "Alta", null, "Dr. Carlos");

        DomainException ex = assertThrows(DomainException.class, () ->
            attendance.cancel("Motivo", "Admin")
        );
        assertEquals("Attendance cannot be cancelled in current status", ex.getMessage());
    }

    @Test
    void cancel_shouldFail_whenReasonIsBlank() {
        Attendance attendance = checkInAttendance();

        assertThrows(DomainException.class, () ->
            attendance.cancel("  ", "Recepcao")
        );
    }

    // --- status history ---

    @Test
    void fullFlow_shouldRecordCompleteStatusHistory() {
        Attendance attendance = checkInAttendance();                    // 1
        attendance.startTriage(NURSE_ID, "Enf");                       // 2
        attendance.finishTriage(NURSE_ID, RiskLevel.GREEN, null, "Enf"); // 3
        attendance.callDoctor(DOCTOR_ID, "Sala 1", "Rec");             // 4
        attendance.startConsultation(DOCTOR_ID, "Dr");                  // 5
        attendance.finishConsultation(DOCTOR_ID, "Alta", null, "Dr");   // 6

        assertEquals(6, attendance.getStatusHistory().size());
        assertEquals(AttendanceStatus.COMPLETED, attendance.getStatus());
    }

    // --- helpers ---

    private Attendance checkInAttendance() {
        return Attendance.checkIn(PATIENT_ID, "A001", null, "Recepcao");
    }

    private Attendance advancedToWaitingDoctor() {
        Attendance a = checkInAttendance();
        a.startTriage(NURSE_ID, "Enf");
        a.finishTriage(NURSE_ID, RiskLevel.GREEN, null, "Enf");
        return a;
    }

    private Attendance advancedToCalledDoctor() {
        Attendance a = advancedToWaitingDoctor();
        a.callDoctor(DOCTOR_ID, "Sala 1", "Rec");
        return a;
    }

    private Attendance advancedToInConsultation() {
        Attendance a = advancedToCalledDoctor();
        a.startConsultation(DOCTOR_ID, "Dr");
        return a;
    }
}
