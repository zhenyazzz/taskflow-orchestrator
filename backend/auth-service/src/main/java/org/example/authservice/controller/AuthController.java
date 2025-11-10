package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.dto.AssignRoleRequest;
import org.example.authservice.dto.AuthResponse;
import org.example.authservice.dto.JwtResponse;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.dto.RemoveRoleRequest;
import org.example.authservice.service.AuthService;
import org.example.authservice.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "API для регистрации, входа и управления ролями пользователей")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/signUp")
    @Operation(
        summary = "Регистрация пользователя",
        description = "Создает нового пользователя в системе с указанными учетными данными"
    )
    public ResponseEntity<JwtResponse> register(
            @Valid @RequestBody RegisterRequest singUpRequest,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown") String userAgent,
            HttpServletResponse response) {
        log.info("Получен запрос на регистрацию пользователя: {}", singUpRequest.username());
        
        AuthResponse authResponse = authService.registerUser(singUpRequest, userAgent);
        cookieUtil.setRefreshTokenCookie(response, authResponse.refreshToken());
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponse.jwtResponse());
    }

    @PostMapping("/signIn")
    @Operation(
        summary = "Вход пользователя",
        description = "Аутентифицирует пользователя и возвращает JWT токен для доступа к защищенным ресурсам"
    )
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            @RequestHeader("User-Agent") String userAgent,
            HttpServletResponse response) {
        log.info("Получен запрос на вход пользователя: {}", loginRequest.username());
        
        AuthResponse authResponse = authService.loginUser(loginRequest, userAgent);
        cookieUtil.setRefreshTokenCookie(response, authResponse.refreshToken());
        
        return ResponseEntity.ok(authResponse.jwtResponse());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assign-role")
    @Operation(
        summary = "Назначение роли пользователю",
        description = "Назначает или изменяет роль существующему пользователю в системе"
    )
    public ResponseEntity<JwtResponse> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        log.info("Получен запрос на назначение роли пользователю: {}", request.username());
        return ResponseEntity.ok(authService.assignRole(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/remove-role")
    @Operation(
        summary = "Удаление роли у пользователя",
        description = "Удаляет роль у существующего пользователя в системе"
    )
    public ResponseEntity<JwtResponse> removeRole(@Valid @RequestBody RemoveRoleRequest request) {
        log.info("Получен запрос на назначение роли пользователю: {}", request.username());
        return ResponseEntity.ok(authService.removeRole(request));
    }

    @GetMapping("/validate")
    @Operation(
        summary = "Валидация JWT токена",
        description = "Проверяет валидность JWT токена из заголовка Authorization"
    )
    public ResponseEntity<JwtResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Получен запрос на валидацию токена: {}", authHeader);
        return ResponseEntity.ok(authService.validateToken(authHeader));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Обновление JWT токена",
        description = "Обновляет access токен, используя refresh токен из куки."
    )
    public ResponseEntity<JwtResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        log.info("Получен запрос на обновление токена");
        
        AuthResponse authResponse = authService.refreshToken(request);
        cookieUtil.setRefreshTokenCookie(response, authResponse.refreshToken());
        
        return ResponseEntity.ok(authResponse.jwtResponse());
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Выход пользователя",
        description = "Отзывает refresh токен и удаляет cookie"
    )
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Получен запрос на выход пользователя");
        
        authService.logout(request);
        cookieUtil.deleteRefreshTokenCookie(response);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    @Operation(
        summary = "Выход со всех устройств",
        description = "Отзывает все refresh токены пользователя"
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutAll(HttpServletRequest request, HttpServletResponse response) {
        log.info("Получен запрос на выход со всех устройств");
        
        authService.logoutAll(request);
        cookieUtil.deleteRefreshTokenCookie(response);
        
        return ResponseEntity.noContent().build();
    }
}