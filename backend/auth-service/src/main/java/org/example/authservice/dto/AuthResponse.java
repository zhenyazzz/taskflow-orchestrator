package org.example.authservice.dto;

public record AuthResponse(JwtResponse jwtResponse, String refreshToken) {
}

