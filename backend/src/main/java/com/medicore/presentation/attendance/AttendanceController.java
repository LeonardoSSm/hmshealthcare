package com.medicore.presentation.attendance;

import com.medicore.application.attendance.AttendanceResponse;
import com.medicore.application.attendance.CallDoctorCommand;
import com.medicore.application.attendance.CallDoctorUseCase;
import com.medicore.application.attendance.CancelAttendanceCommand;
import com.medicore.application.attendance.CancelAttendanceUseCase;
import com.medicore.application.attendance.CheckInAttendanceCommand;
import com.medicore.application.attendance.CheckInAttendanceUseCase;
import com.medicore.application.attendance.FinishConsultationCommand;
import com.medicore.application.attendance.FinishConsultationUseCase;
import com.medicore.application.attendance.FinishTriageCommand;
import com.medicore.application.attendance.FinishTriageUseCase;
import com.medicore.application.attendance.GetAttendanceByIdUseCase;
import com.medicore.application.attendance.ListAttendancesUseCase;
import com.medicore.application.attendance.StartConsultationCommand;
import com.medicore.application.attendance.StartConsultationUseCase;
import com.medicore.application.attendance.StartTriageCommand;
import com.medicore.application.attendance.StartTriageUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final CheckInAttendanceUseCase checkInAttendanceUseCase;
    private final ListAttendancesUseCase listAttendancesUseCase;
    private final GetAttendanceByIdUseCase getAttendanceByIdUseCase;
    private final StartTriageUseCase startTriageUseCase;
    private final FinishTriageUseCase finishTriageUseCase;
    private final CallDoctorUseCase callDoctorUseCase;
    private final StartConsultationUseCase startConsultationUseCase;
    private final FinishConsultationUseCase finishConsultationUseCase;
    private final CancelAttendanceUseCase cancelAttendanceUseCase;

    public AttendanceController(
        CheckInAttendanceUseCase checkInAttendanceUseCase,
        ListAttendancesUseCase listAttendancesUseCase,
        GetAttendanceByIdUseCase getAttendanceByIdUseCase,
        StartTriageUseCase startTriageUseCase,
        FinishTriageUseCase finishTriageUseCase,
        CallDoctorUseCase callDoctorUseCase,
        StartConsultationUseCase startConsultationUseCase,
        FinishConsultationUseCase finishConsultationUseCase,
        CancelAttendanceUseCase cancelAttendanceUseCase
    ) {
        this.checkInAttendanceUseCase = checkInAttendanceUseCase;
        this.listAttendancesUseCase = listAttendancesUseCase;
        this.getAttendanceByIdUseCase = getAttendanceByIdUseCase;
        this.startTriageUseCase = startTriageUseCase;
        this.finishTriageUseCase = finishTriageUseCase;
        this.callDoctorUseCase = callDoctorUseCase;
        this.startConsultationUseCase = startConsultationUseCase;
        this.finishConsultationUseCase = finishConsultationUseCase;
        this.cancelAttendanceUseCase = cancelAttendanceUseCase;
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public AttendanceResponse checkIn(@Valid @RequestBody AttendanceRequests.CheckInRequest request) {
        return checkInAttendanceUseCase.execute(new CheckInAttendanceCommand(
            request.patientId(),
            request.notes(),
            request.requestedBy()
        ));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<AttendanceResponse> list(@RequestParam(defaultValue = "false") boolean includeClosed) {
        return listAttendancesUseCase.execute(includeClosed);
    }

    @GetMapping("/{attendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public AttendanceResponse getById(@PathVariable UUID attendanceId) {
        return getAttendanceByIdUseCase.execute(attendanceId);
    }

    @PostMapping("/{attendanceId}/start-triage")
    @PreAuthorize("hasAnyRole('ADMIN','NURSE')")
    public AttendanceResponse startTriage(
        @PathVariable UUID attendanceId,
        @Valid @RequestBody AttendanceRequests.StartTriageRequest request
    ) {
        return startTriageUseCase.execute(attendanceId, new StartTriageCommand(request.nurseId(), request.requestedBy()));
    }

    @PostMapping("/{attendanceId}/finish-triage")
    @PreAuthorize("hasAnyRole('ADMIN','NURSE')")
    public AttendanceResponse finishTriage(
        @PathVariable UUID attendanceId,
        @Valid @RequestBody AttendanceRequests.FinishTriageRequest request
    ) {
        return finishTriageUseCase.execute(attendanceId, new FinishTriageCommand(
            request.nurseId(),
            request.riskLevel(),
            request.notes(),
            request.requestedBy()
        ));
    }

    @PostMapping("/{attendanceId}/call-doctor")
    @PreAuthorize("hasAnyRole('ADMIN','NURSE','RECEPTIONIST')")
    public AttendanceResponse callDoctor(
        @PathVariable UUID attendanceId,
        @Valid @RequestBody AttendanceRequests.CallDoctorRequest request
    ) {
        return callDoctorUseCase.execute(attendanceId, new CallDoctorCommand(
            request.doctorId(),
            request.roomLabel(),
            request.requestedBy()
        ));
    }

    @PostMapping("/{attendanceId}/start-consultation")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public AttendanceResponse startConsultation(
        @PathVariable UUID attendanceId,
        @Valid @RequestBody AttendanceRequests.StartConsultationRequest request
    ) {
        return startConsultationUseCase.execute(attendanceId, new StartConsultationCommand(
            request.doctorId(),
            request.requestedBy()
        ));
    }

    @PostMapping("/{attendanceId}/finish-consultation")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public AttendanceResponse finishConsultation(
        @PathVariable UUID attendanceId,
        @Valid @RequestBody AttendanceRequests.FinishConsultationRequest request
    ) {
        return finishConsultationUseCase.execute(attendanceId, new FinishConsultationCommand(
            request.doctorId(),
            request.outcome(),
            request.notes(),
            request.requestedBy()
        ));
    }

    @PostMapping("/{attendanceId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public AttendanceResponse cancel(
        @PathVariable UUID attendanceId,
        @Valid @RequestBody AttendanceRequests.CancelRequest request
    ) {
        return cancelAttendanceUseCase.execute(attendanceId, new CancelAttendanceCommand(
            request.reason(),
            request.requestedBy()
        ));
    }
}
