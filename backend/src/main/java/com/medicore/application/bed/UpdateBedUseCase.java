package com.medicore.application.bed;

import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.admission.BedType;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateBedUseCase {
    private final BedRepository bedRepository;

    public UpdateBedUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Transactional
    public BedResponse execute(UUID bedId, CreateBedCommand command) {
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new DomainException("Bed not found"));
        bed.updateDetails(
            command.number(),
            command.floor(),
            command.ward(),
            BedType.valueOf(command.type().trim().toUpperCase())
        );
        return BedResponse.from(bedRepository.save(bed));
    }
}
