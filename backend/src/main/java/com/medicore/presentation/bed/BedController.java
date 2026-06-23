package com.medicore.presentation.bed;

import com.medicore.application.bed.BedResponse;
import com.medicore.application.bed.ChangeBedStatusUseCase;
import com.medicore.application.bed.CreateBedCommand;
import com.medicore.application.bed.CreateBedUseCase;
import com.medicore.application.bed.ListBedsUseCase;
import com.medicore.application.bed.UpdateBedUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/beds")
public class BedController {
    private final ListBedsUseCase listBedsUseCase;
    private final CreateBedUseCase createBedUseCase;
    private final UpdateBedUseCase updateBedUseCase;
    private final ChangeBedStatusUseCase changeBedStatusUseCase;

    public BedController(
        ListBedsUseCase listBedsUseCase,
        CreateBedUseCase createBedUseCase,
        UpdateBedUseCase updateBedUseCase,
        ChangeBedStatusUseCase changeBedStatusUseCase
    ) {
        this.listBedsUseCase = listBedsUseCase;
        this.createBedUseCase = createBedUseCase;
        this.updateBedUseCase = updateBedUseCase;
        this.changeBedStatusUseCase = changeBedStatusUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<BedResponse> list() {
        return listBedsUseCase.execute();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BedResponse create(@Valid @RequestBody BedRequest request) {
        return createBedUseCase.execute(new CreateBedCommand(
            request.number(), request.floor(), request.ward(), request.type()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BedResponse update(@PathVariable UUID id, @Valid @RequestBody BedRequest request) {
        return updateBedUseCase.execute(id, new CreateBedCommand(
            request.number(), request.floor(), request.ward(), request.type()
        ));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','NURSE')")
    public BedResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeBedStatusRequest request) {
        return changeBedStatusUseCase.execute(id, request.status());
    }
}
