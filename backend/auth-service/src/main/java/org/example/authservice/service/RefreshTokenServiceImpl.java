package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.exception.exceptions.InvalidTokenException;
import org.example.authservice.model.RefreshToken;
import org.example.authservice.model.User;
import org.example.authservice.repository.RefreshTokenRepository;
import org.example.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public RefreshTokenPair createRefreshToken(User user, String deviceInfo) {
        String token = jwtUtil.generateRefreshToken(user.getId());
        String tokenHash = hashToken(token);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Создан refresh токен для пользователя: {}", user.getUsername());
        return new RefreshTokenPair(token, savedToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyRefreshToken(String token) {
        if (!jwtUtil.validateRefreshToken(token)) {
            throw new InvalidTokenException("Неверный refresh токен");
        }

        String tokenHash = hashToken(token);
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh токен не найден или отозван"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
            throw new InvalidTokenException("Refresh токен истек");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        String tokenHash = hashToken(token);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshToken.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(refreshToken);
                    log.info("Refresh токен отозван для пользователя: {}", refreshToken.getUser().getUsername());
                });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        LocalDateTime now = LocalDateTime.now();
        int revokedCount = refreshTokenRepository.revokeAllUserTokens(user, now);
        log.info("Отозвано {} refresh токенов для пользователя: {}", revokedCount, user.getUsername());
    }

    @Override
    @Transactional
    @Scheduled(cron = "${jwt.cleanup-cron:0 0 2 * * ?}") 
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = refreshTokenRepository.deleteExpiredAndRevokedTokens(now);
        log.info("Удалено {} устаревших refresh токенов", deletedCount);
    }

    @Override
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Ошибка при хешировании токена", e);
            throw new RuntimeException("Ошибка при хешировании токена", e);
        }
    }
}

