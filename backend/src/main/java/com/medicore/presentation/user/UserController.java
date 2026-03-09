package com.medicore.presentation.user;

import com.medicore.application.user.CreateStaffUserCommand;
import com.medicore.application.user.StaffUserResponse;
import com.medicore.application.user.UpdateStaffUserCommand;
import com.medicore.application.user.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffUserResponse> list() {
        return userManagementService.listAll();
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<StaffUserResponse> listDoctors() {
        return userManagementService.listActiveDoctors();
    }

    @GetMapping("/nurses")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')")
    public List<StaffUserResponse> listNurses() {
        return userManagementService.listActiveNurses();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userManagementService.create(new CreateStaffUserCommand(
            request.name(),
            request.email(),
            request.password(),
            request.role(),
            request.active()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StaffUserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userManagementService.update(id, new UpdateStaffUserCommand(
            request.name(),
            request.email(),
            request.password(),
            request.role(),
            request.active()
        ));
    }
}
