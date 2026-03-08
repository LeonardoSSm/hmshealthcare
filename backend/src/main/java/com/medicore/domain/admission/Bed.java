package com.medicore.domain.admission;

import com.medicore.domain.shared.DomainException;

import java.util.Objects;
import java.util.UUID;

public class Bed {
    private final UUID id;
    private final String number;
    private final int floor;
    private final String ward;
    private final BedType type;

    private BedStatus status;

    public Bed(UUID id, String number, int floor, String ward, BedType type, BedStatus status) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        if (number == null || number.isBlank()) {
            throw new DomainException("Bed number cannot be blank");
        }
        if (ward == null || ward.isBlank()) {
            throw new DomainException("Ward cannot be blank");
        }
        this.number = number.trim();
        this.floor = floor;
        this.ward = ward.trim();
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
    }

    public void occupy() {
        if (status != BedStatus.AVAILABLE) {
            throw new DomainException("Bed is not available");
        }
        this.status = BedStatus.OCCUPIED;
    }

    public void releaseToCleaning() {
        this.status = BedStatus.CLEANING;
    }

    public void markAvailable() {
        this.status = BedStatus.AVAILABLE;
    }

    public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public int getFloor() {
        return floor;
    }

    public String getWard() {
        return ward;
    }

    public BedType getType() {
        return type;
    }

    public BedStatus getStatus() {
        return status;
    }
}
