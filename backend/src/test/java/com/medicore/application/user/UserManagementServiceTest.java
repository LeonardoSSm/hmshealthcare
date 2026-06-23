package com.medicore.application.user;

import com.medicore.domain.shared.DomainException;
import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService service;

    // --- listActiveDoctors ---

    @Test
    void listActiveDoctors_shouldReturnOnlyActiveDoctors() {
        UserEntity doctor = buildUser("11111111-1111-1111-1111-111111111111", "Dra. Ana", "dr.ana@medicore.local", "DOCTOR", true);

        when(userJpaRepository.findByRoleAndActiveTrueOrderByNameAsc("DOCTOR"))
                .thenReturn(List.of(doctor));

        List<StaffUserResponse> result = service.listActiveDoctors();

        assertEquals(1, result.size());
        assertEquals("DOCTOR", result.get(0).role());
        assertEquals("dr.ana@medicore.local", result.get(0).email());
        assertTrue(result.get(0).active());
    }

    @Test
    void listActiveDoctors_shouldReturnEmpty_whenNoDoctorsExist() {
        when(userJpaRepository.findByRoleAndActiveTrueOrderByNameAsc("DOCTOR"))
                .thenReturn(List.of());

        assertTrue(service.listActiveDoctors().isEmpty());
    }

    // --- listAll ---

    @Test
    void listAll_shouldReturnAllUsers() {
        UserEntity admin = buildUser("00000000-0000-0000-0000-000000000001", "Admin", "admin@medicore.local", "ADMIN", true);
        UserEntity doctor = buildUser("11111111-1111-1111-1111-111111111111", "Dra. Ana", "dr.ana@medicore.local", "DOCTOR", true);

        when(userJpaRepository.findAllByOrderByNameAsc()).thenReturn(List.of(admin, doctor));

        List<StaffUserResponse> result = service.listAll();

        assertEquals(2, result.size());
    }

    // --- create ---

    @Test
    void create_shouldPersistUserWithEncodedPassword() {
        CreateStaffUserCommand command = new CreateStaffUserCommand("Dr. Carlos", "dr.carlos@medicore.local", "senha123", "DOCTOR", true);

        when(userJpaRepository.existsByEmail("dr.carlos@medicore.local")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("ENCODED");
        when(userJpaRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity saved = inv.getArgument(0);
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        StaffUserResponse response = service.create(command);

        assertEquals("Dr. Carlos", response.name());
        assertEquals("dr.carlos@medicore.local", response.email());
        assertEquals("DOCTOR", response.role());
        assertTrue(response.active());
    }

    @Test
    void create_shouldThrow_whenEmailAlreadyInUse() {
        CreateStaffUserCommand command = new CreateStaffUserCommand("Alguem", "dup@medicore.local", "senha123", "NURSE", true);

        when(userJpaRepository.existsByEmail("dup@medicore.local")).thenReturn(true);

        DomainException ex = assertThrows(DomainException.class, () -> service.create(command));
        assertEquals("Email already in use", ex.getMessage());
    }

    @Test
    void create_shouldThrow_whenRoleIsInvalid() {
        CreateStaffUserCommand command = new CreateStaffUserCommand("X", "x@medicore.local", "senha123", "HACKER", true);

        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(DomainException.class, () -> service.create(command));
    }

    @Test
    void create_shouldThrow_whenPasswordIsTooShort() {
        CreateStaffUserCommand command = new CreateStaffUserCommand("Y", "y@medicore.local", "abc", "DOCTOR", true);

        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);

        DomainException ex = assertThrows(DomainException.class, () -> service.create(command));
        assertEquals("Password must have at least 8 characters", ex.getMessage());
    }

    // --- update ---

    @Test
    void update_shouldUpdateUserFields() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UserEntity existing = buildUser(id.toString(), "Dra. Ana", "dr.ana@medicore.local", "DOCTOR", true);
        UpdateStaffUserCommand command = new UpdateStaffUserCommand("Dra. Ana Lima", "ana.lima@medicore.local", null, "DOCTOR", true);

        when(userJpaRepository.findById(id.toString())).thenReturn(Optional.of(existing));
        when(userJpaRepository.existsByEmail("ana.lima@medicore.local")).thenReturn(false);
        when(userJpaRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        StaffUserResponse response = service.update(id, command);

        assertEquals("Dra. Ana Lima", response.name());
        assertEquals("ana.lima@medicore.local", response.email());
    }

    @Test
    void update_shouldThrow_whenUserNotFound() {
        UUID id = UUID.randomUUID();
        UpdateStaffUserCommand command = new UpdateStaffUserCommand("X", "x@medicore.local", null, "DOCTOR", true);

        when(userJpaRepository.findById(id.toString())).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> service.update(id, command));
        assertEquals("User not found", ex.getMessage());
    }

    // --- helpers ---

    private UserEntity buildUser(String id, String name, String email, String role, boolean active) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setRole(role);
        u.setActive(active);
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }
}
