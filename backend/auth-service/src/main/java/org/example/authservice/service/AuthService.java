package org.example.authservice.service;

import org.example.authservice.dto.AssignRoleRequest;
import org.example.authservice.dto.JwtResponse;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.dto.RemoveRoleRequest;
import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserDeletedEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import jakarta.servlet.http.HttpServletRequest;


import org.example.authservice.dto.AuthResponse;

public interface AuthService {
    AuthResponse registerUser(RegisterRequest registerRequest, String deviceInfo);
    AuthResponse loginUser(LoginRequest loginRequest, String userAgent);
    JwtResponse validateToken(String token);
    JwtResponse assignRole(AssignRoleRequest assignRoleRequest);
    JwtResponse removeRole(RemoveRoleRequest request);
    AuthResponse refreshToken(HttpServletRequest request);
    void logout(HttpServletRequest request);
    void logoutAll(HttpServletRequest request);
    void handleUserDelete(UserDeletedEvent event);
    void handleUserProfileUpdate(UserProfileUpdatedEvent event);
    void handleUserCreation(UserCreatedEvent event);
}
