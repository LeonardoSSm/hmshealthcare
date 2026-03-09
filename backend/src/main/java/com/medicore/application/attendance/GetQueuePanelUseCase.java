package com.medicore.application.attendance;

import com.medicore.domain.attendance.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetQueuePanelUseCase {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceResponseMapper responseMapper;

    public GetQueuePanelUseCase(AttendanceRepository attendanceRepository, AttendanceResponseMapper responseMapper) {
        this.attendanceRepository = attendanceRepository;
        this.responseMapper = responseMapper;
    }

    @Transactional(readOnly = true)
    public List<QueuePanelItemResponse> execute() {
        return attendanceRepository.findOpenAttendances().stream()
            .map(responseMapper::toPanelItem)
            .toList();
    }
}
