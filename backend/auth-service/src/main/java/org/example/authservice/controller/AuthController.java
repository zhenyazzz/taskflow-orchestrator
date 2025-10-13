package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.dto.AssignRoleRequest;
import org.example.authservice.dto.JwtResponse;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.dto.RemoveRoleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;


import org.example.authservice.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "API для регистрации, входа и управления ролями пользователей")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signUp")
    @Operation(
        summary = "Регистрация пользователя",
        description = "Создает нового пользователя в системе с указанными учетными данными"
    )
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest singUpRequest) {
        log.info("Получен запрос на регистрацию пользователя: {}", singUpRequest.username());
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registerUser(singUpRequest));
    }

    @PostMapping("/signIn")
    @Operation(
        summary = "Вход пользователя",
        description = "Аутентифицирует пользователя и возвращает JWT токен для доступа к защищенным ресурсам"
    )
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest, @RequestHeader("User-Agent") String userAgent) {
        log.info("Получен запрос на вход пользователя: {}", loginRequest.username());
        return ResponseEntity.ok(authService.loginUser(loginRequest, userAgent));
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
    public ResponseEntity<JwtResponse> refresh(HttpServletRequest request) {
        log.info("Получен запрос на обновление токена");
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}