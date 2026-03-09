package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelAttendanceUseCase {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceResponseMapper responseMapper;

    public CancelAttendanceUseCase(AttendanceRepository attendanceRepository, AttendanceResponseMapper responseMapper) {
        this.attendanceRepository = attendanceRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public AttendanceResponse execute(UUID attendanceId, CancelAttendanceCommand command) {
        Attendance attendance = attendanceRepository.findByIdForUpdate(attendanceId)
            .orElseThrow(() -> new DomainException("Attendance not found"));
        attendance.cancel(command.reason(), fallbackActor(command.requestedBy(), "Recepcao"));
        return responseMapper.toResponse(attendanceRepository.save(attendance));
    }

    private static String fallbackActor(String requestedBy, String fallback) {
        return requestedBy == null || requestedBy.isBlank() ? fallback : requestedBy.trim();
    }
}
