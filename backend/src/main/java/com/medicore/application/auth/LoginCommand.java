package com.medicore.application.auth;

public record LoginCommand(String email, String password) {
}
