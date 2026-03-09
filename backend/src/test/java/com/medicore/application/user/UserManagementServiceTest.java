package com.medicore.application.user;

import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {
    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService service;

    @Test
    void listActiveDoctorsShouldReturnOnlyActiveDoctors() {
        UserEntity doctor = new UserEntity();
        doctor.setId("11111111-1111-1111-1111-111111111111");
        doctor.setName("Dra. Ana");
        doctor.setEmail("dr.ana@medicore.local");
        doctor.setRole("DOCTOR");
        doctor.setActive(true);

        UserEntity admin = new UserEntity();
        admin.setId("00000000-0000-0000-0000-000000000001");
        admin.setName("Admin");
        admin.setEmail("admin@medicore.local");
        admin.setRole("ADMIN");
        admin.setActive(true);

        UserEntity inactiveDoctor = new UserEntity();
        inactiveDoctor.setId("22222222-2222-2222-2222-222222222222");
        inactiveDoctor.setName("Dr. Inativo");
        inactiveDoctor.setEmail("dr.inativo@medicore.local");
        inactiveDoctor.setRole("DOCTOR");
        inactiveDoctor.setActive(false);

        when(userJpaRepository.findAllByOrderByNameAsc()).thenReturn(List.of(doctor, admin, inactiveDoctor));

        List<StaffUserResponse> result = service.listActiveDoctors();

        assertEquals(1, result.size());
        assertEquals("DOCTOR", result.get(0).role());
        assertEquals("dr.ana@medicore.local", result.get(0).email());
    }
}
