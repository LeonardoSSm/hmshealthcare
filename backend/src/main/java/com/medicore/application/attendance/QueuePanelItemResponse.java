package com.medicore.application.attendance;

import java.time.LocalDateTime;
import java.util.UUID;

public record QueuePanelItemResponse(
    UUID attendanceId,
    String ticketNumber,
    String displayName,
    String status,
    String riskLevel,
    String roomLabel,
    LocalDateTime calledAt,
    LocalDateTime checkInAt
) {
}
