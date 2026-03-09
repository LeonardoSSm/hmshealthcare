package com.medicore.presentation.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
    int status,
    String code,
    String message,
    String traceId,
    OffsetDateTime timestamp,
    Map<String, String> details
) {
    public static ApiErrorResponse of(
        int status,
        String code,
        String message,
        String traceId,
        Map<String, String> details
    ) {
        return new ApiErrorResponse(
            status,
            code,
            message,
            traceId,
            OffsetDateTime.now(ZoneOffset.UTC),
            details == null || details.isEmpty() ? null : details
        );
    }
}
