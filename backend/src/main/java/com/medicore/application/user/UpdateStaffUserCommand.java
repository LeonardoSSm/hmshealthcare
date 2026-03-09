package com.medicore.application.user;

public record UpdateStaffUserCommand(
    String name,
    String email,
    String password,
    String role,
    boolean active
) {
}
