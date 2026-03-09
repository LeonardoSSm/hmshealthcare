package com.medicore.application.user;

public record CreateStaffUserCommand(
    String name,
    String email,
    String password,
    String role,
    boolean active
) {
}
