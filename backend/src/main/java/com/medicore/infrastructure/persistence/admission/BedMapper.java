package com.medicore.infrastructure.persistence.admission;

import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedStatus;
import com.medicore.domain.admission.BedType;

import java.util.UUID;

public final class BedMapper {
    private BedMapper() {
    }

    public static BedEntity toEntity(Bed bed) {
        BedEntity entity = new BedEntity();
        entity.setId(bed.getId().toString());
        entity.setNumber(bed.getNumber());
        entity.setFloor(bed.getFloor());
        entity.setWard(bed.getWard());
        entity.setType(bedTypeToPersistence(bed.getType()));
        entity.setStatus(bed.getStatus().name());
        return entity;
    }

    public static Bed toDomain(BedEntity entity) {
        return new Bed(
            UUID.fromString(entity.getId()),
            entity.getNumber(),
            entity.getFloor(),
            entity.getWard(),
            bedTypeFromPersistence(entity.getType()),
            BedStatus.valueOf(entity.getStatus())
        );
    }

    private static BedType bedTypeFromPersistence(String value) {
        if ("UTI".equalsIgnoreCase(value)) {
            return BedType.ICU;
        }
        return BedType.valueOf(value);
    }

    private static String bedTypeToPersistence(BedType bedType) {
        if (bedType == BedType.ICU) {
            return "UTI";
        }
        return bedType.name();
    }
}
