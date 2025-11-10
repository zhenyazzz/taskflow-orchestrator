package org.example.authservice.service;

import org.example.authservice.model.RefreshToken;

public record RefreshTokenPair(String token, RefreshToken tokenEntity) {
}

