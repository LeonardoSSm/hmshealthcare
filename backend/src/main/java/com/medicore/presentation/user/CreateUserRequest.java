package com.medicore.presentation.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotBlank String role,
    boolean active
) {
}
