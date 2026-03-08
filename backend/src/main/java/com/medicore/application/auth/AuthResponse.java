package com.medicore.application.auth;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String role,
    String name,
    String email
) {
}
