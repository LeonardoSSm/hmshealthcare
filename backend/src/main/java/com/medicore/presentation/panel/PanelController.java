package com.medicore.presentation.panel;

import com.medicore.application.attendance.GetQueuePanelUseCase;
import com.medicore.application.attendance.QueuePanelItemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/panel")
public class PanelController {
    private final GetQueuePanelUseCase getQueuePanelUseCase;

    public PanelController(GetQueuePanelUseCase getQueuePanelUseCase) {
        this.getQueuePanelUseCase = getQueuePanelUseCase;
    }

    @GetMapping("/queue")
    public List<QueuePanelItemResponse> queue() {
        return getQueuePanelUseCase.execute();
    }
}
