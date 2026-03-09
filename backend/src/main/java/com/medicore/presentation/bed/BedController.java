package com.medicore.presentation.bed;

import com.medicore.application.bed.BedResponse;
import com.medicore.application.bed.ListBedsUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/beds")
public class BedController {
    private final ListBedsUseCase listBedsUseCase;

    public BedController(ListBedsUseCase listBedsUseCase) {
        this.listBedsUseCase = listBedsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<BedResponse> list() {
        return listBedsUseCase.execute();
    }
}
