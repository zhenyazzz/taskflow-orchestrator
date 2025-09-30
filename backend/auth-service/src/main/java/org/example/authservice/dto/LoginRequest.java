package org.example.authservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос для входа пользователя")
public record LoginRequest(
        @Schema(description = "Имя пользователя", example = "john_doe")
        @NotBlank(message = "Имя обязательно")
        String username,

        @Schema(description = "Пароль пользователя", example = "password123")
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 4, message = "Пароль должен быть не менее 6 символов")
        String password
) {}

