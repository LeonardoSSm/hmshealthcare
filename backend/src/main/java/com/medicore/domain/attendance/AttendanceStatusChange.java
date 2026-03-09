package com.medicore.domain.attendance;

import java.time.LocalDateTime;

public record AttendanceStatusChange(
    AttendanceStatus fromStatus,
    AttendanceStatus toStatus,
    String changedBy,
    String note,
    LocalDateTime changedAt
) {
}
