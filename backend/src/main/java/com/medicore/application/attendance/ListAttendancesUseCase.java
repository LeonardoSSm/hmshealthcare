package com.medicore.application.attendance;

import com.medicore.domain.attendance.Attendance;
import com.medicore.domain.attendance.AttendanceRepository;
import com.medicore.domain.shared.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public PagedResult<AttendanceResponse> executeOpen() {
        List<AttendanceResponse> items = attendanceRepository.findOpenAttendances().stream()
            .map(responseMapper::toResponse)
            .toList();
        return new PagedResult<>(items, 0, items.size(), items.size(), 1);
    }

    @Transactional(readOnly = true)
    public PagedResult<AttendanceResponse> executePaged(int page, int size) {
        PagedResult<Attendance> result = attendanceRepository.findAllPaged(page, size);
        return new PagedResult<>(
            result.content().stream().map(responseMapper::toResponse).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()
        );
    }

    @Transactional(readOnly = true)
    public PagedResult<AttendanceResponse> executeByDateRange(LocalDate from, LocalDate to, int page, int size) {
        PagedResult<Attendance> result = attendanceRepository.findByDateRange(from, to, page, size);
        return new PagedResult<>(
            result.content().stream().map(responseMapper::toResponse).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()
        );
    }
}
