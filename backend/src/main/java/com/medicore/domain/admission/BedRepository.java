package com.medicore.domain.admission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BedRepository {
    Bed save(Bed bed);
    Optional<Bed> findById(UUID id);
    Optional<Bed> findByIdForUpdate(UUID id);
    List<Bed> findAll();
}
