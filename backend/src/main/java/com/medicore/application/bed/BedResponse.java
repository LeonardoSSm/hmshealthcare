package com.medicore.application.bed;

import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedType;

import java.util.UUID;

public record BedResponse(
    UUID id,
    String number,
    int floor,
    String ward,
    String type,
    String status
) {
    public static BedResponse from(Bed bed) {
        return new BedResponse(
            bed.getId(),
            bed.getNumber(),
            bed.getFloor(),
            bed.getWard(),
            bed.getType() == BedType.ICU ? "UTI" : bed.getType().name(),
            bed.getStatus().name()
        );
    }
}
