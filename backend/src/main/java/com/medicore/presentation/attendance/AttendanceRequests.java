package com.medicore.presentation.attendance;

import com.medicore.domain.attendance.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public final class AttendanceRequests {
    private AttendanceRequests() {
    }

    public record CheckInRequest(
        @NotNull UUID patientId,
        @Size(max = 500) String notes,
        @Size(max = 120) String requestedBy
    ) {
    }

    public record StartTriageRequest(
        @NotNull UUID nurseId,
        @Size(max = 120) String requestedBy
    ) {
    }

    public record FinishTriageRequest(
        @NotNull UUID nurseId,
        @NotNull RiskLevel riskLevel,
        @Size(max = 2000) String notes,
        @Size(max = 120) String requestedBy
    ) {
    }

    public record CallDoctorRequest(
        @NotNull UUID doctorId,
        @NotBlank @Size(max = 40) String roomLabel,
        @Size(max = 120) String requestedBy
    ) {
    }

    public record StartConsultationRequest(
        @NotNull UUID doctorId,
        @Size(max = 120) String requestedBy
    ) {
    }

    public record FinishConsultationRequest(
        @NotNull UUID doctorId,
        @NotBlank @Size(max = 2000) String outcome,
        @Size(max = 2000) String notes,
        @Size(max = 120) String requestedBy
    ) {
    }

    public record CancelRequest(
        @NotBlank @Size(max = 500) String reason,
        @Size(max = 120) String requestedBy
    ) {
    }
}
