package org.example.userservice.dto.request;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.events.enums.Role;

import java.util.Set;

@Schema(description = "Request to create a user")
public record CreateUserRequest(
        @Schema(description = "Unique username", example = "john.doe")
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "Password", example = "1234")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 3, max = 12, message = "Password must be between 3 and 50 characters")
        String password,

        @Schema(description = "User email", example = "john.doe@example.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must be at most 100 characters")
        String email,

        @Schema(description = "First name", example = "John")
        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,

        @Schema(description = "Phone number in E.164", example = "+12345678901")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be a valid number with 10-15 digits, optional +")
        String phone,

        @Schema(description = "Assigned roles")
        Set<Role> roles
) {}