package com.medicore.application.attendance;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Component
public class AttendanceTicketGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    public String generate() {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase(Locale.ROOT);
        return "A" + datePart + "-" + suffix;
    }
}
