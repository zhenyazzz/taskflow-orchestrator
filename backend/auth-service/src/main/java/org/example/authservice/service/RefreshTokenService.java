package org.example.authservice.service;

import org.example.authservice.model.RefreshToken;
import org.example.authservice.model.User;

import java.security.NoSuchAlgorithmException;

public interface RefreshTokenService {
    RefreshTokenPair createRefreshToken(User user, String deviceInfo);
    RefreshToken verifyRefreshToken(String token);
    void revokeRefreshToken(String token);
    void revokeAllUserTokens(User user);
    void deleteExpiredTokens();
    String hashToken(String token) throws NoSuchAlgorithmException;
}

