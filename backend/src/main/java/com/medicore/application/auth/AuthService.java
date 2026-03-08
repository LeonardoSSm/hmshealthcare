package com.medicore.application.auth;

import com.medicore.infrastructure.persistence.token.RefreshTokenEntity;
import com.medicore.infrastructure.persistence.token.RefreshTokenJpaRepository;
import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import com.medicore.infrastructure.security.JwtService;
import com.medicore.domain.shared.DomainException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserJpaRepository userJpaRepository;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final JwtService jwtService;

    public AuthService(
        AuthenticationManager authenticationManager,
        UserJpaRepository userJpaRepository,
        RefreshTokenJpaRepository refreshTokenJpaRepository,
        JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userJpaRepository = userJpaRepository;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse login(LoginCommand command) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(command.email(), command.password())
        );

        UserEntity entity = userJpaRepository.findByEmail(command.email())
            .orElseThrow(() -> new DomainException("Invalid credentials"));

        User principal = toPrincipal(entity);
        String accessToken = jwtService.generateAccessToken(principal, Map.of("role", entity.getRole(), "name", entity.getName()));
        String refreshToken = jwtService.generateRefreshToken(principal);

        saveRefreshToken(entity.getId(), refreshToken);

        return new AuthResponse(accessToken, refreshToken, entity.getRole(), entity.getName(), entity.getEmail());
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        String hash = sha256(refreshToken);
        RefreshTokenEntity stored = refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(hash)
            .orElseThrow(() -> new DomainException("Refresh token invalid"));

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            stored.setRevoked(true);
            refreshTokenJpaRepository.save(stored);
            throw new DomainException("Refresh token expired");
        }

        UserEntity user = userJpaRepository.findById(stored.getUserId())
            .orElseThrow(() -> new DomainException("User not found"));

        stored.setRevoked(true);
        refreshTokenJpaRepository.save(stored);

        User principal = toPrincipal(user);
        String newAccess = jwtService.generateAccessToken(principal, Map.of("role", user.getRole(), "name", user.getName()));
        String newRefresh = jwtService.generateRefreshToken(principal);
        saveRefreshToken(user.getId(), newRefresh);

        return new AuthResponse(newAccess, newRefresh, user.getRole(), user.getName(), user.getEmail());
    }

    @Transactional
    public void logout(String refreshToken) {
        String hash = sha256(refreshToken);
        refreshTokenJpaRepository.findByTokenHashAndRevokedFalse(hash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenJpaRepository.save(token);
        });
    }

    private void saveRefreshToken(String userId, String rawToken) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setId(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setTokenHash(sha256(rawToken));
        token.setExpiresAt(jwtService.extractExpiration(rawToken).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        token.setRevoked(false);
        refreshTokenJpaRepository.save(token);
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot hash token", ex);
        }
    }

    private static User toPrincipal(UserEntity user) {
        return new User(
            user.getEmail(),
            user.getPasswordHash(),
            List.of(() -> "ROLE_" + user.getRole())
        );
    }
}
