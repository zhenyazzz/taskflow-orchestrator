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


public interface AuthService {
    JwtResponse registerUser(RegisterRequest registerRequest);
    JwtResponse loginUser(LoginRequest loginRequest, String userAgent);
    JwtResponse validateToken(String token);
    JwtResponse assignRole(AssignRoleRequest assignRoleRequest);
    JwtResponse removeRole(RemoveRoleRequest request);
    JwtResponse refreshToken(HttpServletRequest request);
    void handleUserDelete(UserDeletedEvent event);
    void handleUserProfileUpdate(UserProfileUpdatedEvent event);
    void handleUserCreation(UserCreatedEvent event);
}
