package com.medicore.application.bed;

import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.admission.BedStatus;
import com.medicore.domain.admission.BedType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateBedUseCase {
    private final BedRepository bedRepository;

    public CreateBedUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Transactional
    public BedResponse execute(CreateBedCommand command) {
        Bed bed = new Bed(
            UUID.randomUUID(),
            command.number(),
            command.floor(),
            command.ward(),
            BedType.valueOf(command.type().trim().toUpperCase()),
            BedStatus.AVAILABLE
        );
        return BedResponse.from(bedRepository.save(bed));
    }
}
