package com.medicore.presentation.shared;

import com.medicore.domain.shared.DomainException;
import com.medicore.infrastructure.security.RequestTraceFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(DomainException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "DOMAIN_ERROR", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", "Request validation failed", request, errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return error(
            HttpStatus.CONFLICT,
            "DATA_CONFLICT",
            resolveDataConflictMessage(ex),
            request,
            null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadablePayload(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_PAYLOAD", "Invalid request payload", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected internal error", request, null);
    }

    private ResponseEntity<ApiErrorResponse> error(
        HttpStatus status,
        String code,
        String message,
        HttpServletRequest request,
        Map<String, String> details
    ) {
        return ResponseEntity.status(status).body(
            ApiErrorResponse.of(status.value(), code, message, resolveTraceId(request), details)
        );
    }

    private static String resolveTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute(RequestTraceFilter.TRACE_ID_ATTRIBUTE);
        return traceId == null ? null : traceId.toString();
    }

    private static String resolveDataConflictMessage(DataIntegrityViolationException ex) {
        String text = ex.getMostSpecificCause() == null ? "" : ex.getMostSpecificCause().getMessage();
        if (text.contains("uq_admissions_active_patient")) {
            return "Patient already has an active admission";
        }
        if (text.contains("uq_admissions_active_bed")) {
            return "Bed already has an active admission";
        }
        if (text.contains("users.email")) {
            return "Email already in use";
        }
        if (text.contains("patients.cpf")) {
            return "Patient with this CPF already exists";
        }
        return "Operation violates a database constraint";
    }
}
