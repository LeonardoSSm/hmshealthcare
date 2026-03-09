package com.medicore.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserEntity> findAllByOrderByNameAsc();
    List<UserEntity> findByRoleAndActiveTrueOrderByNameAsc(String role);
}
