package com.medicore.application.bed;

import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import com.medicore.domain.admission.BedStatus;
import com.medicore.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeBedStatusUseCase {
    private final BedRepository bedRepository;

    public ChangeBedStatusUseCase(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Transactional
    public BedResponse execute(UUID bedId, String status) {
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new DomainException("Bed not found"));
        bed.changeStatus(BedStatus.valueOf(status.trim().toUpperCase()));
        return BedResponse.from(bedRepository.save(bed));
    }
}
