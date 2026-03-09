package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetAttendanceByIdUseCase {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceResponseMapper responseMapper;

    public GetAttendanceByIdUseCase(AttendanceRepository attendanceRepository, AttendanceResponseMapper responseMapper) {
        this.attendanceRepository = attendanceRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional(readOnly = true)
    public AttendanceResponse execute(UUID attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new DomainException("Attendance not found"));
        return responseMapper.toResponse(attendance);
    }
}
