package com.medicore.domain.attendance;

public enum AttendanceStatus {
    WAITING_TRIAGE,
    IN_TRIAGE,
    WAITING_DOCTOR,
    CALLED_DOCTOR,
    IN_CONSULTATION,
    COMPLETED,
    CANCELLED;

    public boolean isOpenFlow() {
        return this == WAITING_TRIAGE
            || this == IN_TRIAGE
            || this == WAITING_DOCTOR
            || this == CALLED_DOCTOR
            || this == IN_CONSULTATION;
    }
}
