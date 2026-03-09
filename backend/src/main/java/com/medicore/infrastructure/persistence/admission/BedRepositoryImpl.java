package com.medicore.infrastructure.persistence.admission;

import com.medicore.domain.admission.Bed;
import com.medicore.domain.admission.BedRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BedRepositoryImpl implements BedRepository {
    private final BedJpaRepository bedJpaRepository;

    public BedRepositoryImpl(BedJpaRepository bedJpaRepository) {
        this.bedJpaRepository = bedJpaRepository;
    }

    @Override
    public Bed save(Bed bed) {
        BedEntity saved = bedJpaRepository.save(BedMapper.toEntity(bed));
        return BedMapper.toDomain(saved);
    }

    @Override
    public Optional<Bed> findById(UUID id) {
        return bedJpaRepository.findById(id.toString()).map(BedMapper::toDomain);
    }

    @Override
    public Optional<Bed> findByIdForUpdate(UUID id) {
        return bedJpaRepository.findByIdForUpdate(id.toString()).map(BedMapper::toDomain);
    }

    @Override
    public List<Bed> findAll() {
        return bedJpaRepository.findAll().stream().map(BedMapper::toDomain).toList();
    }
}
