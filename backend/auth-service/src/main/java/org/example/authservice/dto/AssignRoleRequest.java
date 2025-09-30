package org.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.events.enums.Role;

@Schema(description = "Запрос на назначение роли пользователю")
public record AssignRoleRequest(
        @Schema(description = "ID пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "ID обязателен")
        UUID id,

        @Schema(description = "Имя пользователя", example = "john_doe")
        @NotBlank(message = "Имя обязательно")
        String username,

        @Schema(description = "Роль для назначения", example = "ROLE_ADMIN")
        Role role
) {}

