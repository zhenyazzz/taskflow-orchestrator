package org.example.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос для регистрации нового пользователя")
public record RegisterRequest(
        @Schema(description = "Имя пользователя", example = "john_doe")
        @NotBlank(message = "Имя обязательно")
        String username,

        @Schema(description = "Пароль пользователя", example = "password123")
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
        String password,

        @Schema(description = "Электронная почта", example = "john@example.com")
        @NotBlank(message = "Email обязателен")
        @Email(message = "Email должен быть валидным")
        String email,

        @Schema(description = "Имя", example = "Иван")
        @NotBlank(message = "Имя обязательно")
        String firstName,

        @Schema(description = "Фамилия", example = "Иванов")
        @NotBlank(message = "Фамилия обязательна")
        String lastName,

        @Schema(description = "Номер телефона (10 цифр)", example = "1234567890")
        @NotBlank(message = "Телефон обязателен")
        @Pattern(regexp = "^[0-9]{10}$", message = "Телефон должен содержать 10 цифр")
        String phone
) {}

