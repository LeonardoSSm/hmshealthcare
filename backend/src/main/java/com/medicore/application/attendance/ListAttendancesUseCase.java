package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAttendancesUseCase {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceResponseMapper responseMapper;

    public ListAttendancesUseCase(
        AttendanceRepository attendanceRepository,
        AttendanceResponseMapper responseMapper
    ) {
        this.attendanceRepository = attendanceRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> execute(boolean includeClosed) {
        List<Attendance> attendances = includeClosed
            ? attendanceRepository.findAll()
            : attendanceRepository.findOpenAttendances();
        return attendances.stream().map(responseMapper::toResponse).toList();
    }
}
