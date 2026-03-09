package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CallDoctorUseCase {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceStaffValidator staffValidator;
    private final AttendanceResponseMapper responseMapper;

    public CallDoctorUseCase(
        AttendanceRepository attendanceRepository,
        AttendanceStaffValidator staffValidator,
        AttendanceResponseMapper responseMapper
    ) {
        this.attendanceRepository = attendanceRepository;
        this.staffValidator = staffValidator;
        this.responseMapper = responseMapper;
    }

    @Transactional
    public AttendanceResponse execute(UUID attendanceId, CallDoctorCommand command) {
        staffValidator.ensureActiveDoctor(command.doctorId());
        Attendance attendance = attendanceRepository.findByIdForUpdate(attendanceId)
            .orElseThrow(() -> new DomainException("Attendance not found"));
        attendance.callDoctor(command.doctorId(), command.roomLabel(), fallbackActor(command.requestedBy(), "Recepcao"));
        return responseMapper.toResponse(attendanceRepository.save(attendance));
    }

    private static String fallbackActor(String requestedBy, String fallback) {
        return requestedBy == null || requestedBy.isBlank() ? fallback : requestedBy.trim();
    }
}
