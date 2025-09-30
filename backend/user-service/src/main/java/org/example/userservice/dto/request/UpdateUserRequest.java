package org.example.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a user")
public record UpdateUserRequest(
        @Schema(description = "User name", example = "johndoe")
        @NotBlank(message = "User name cannot be blank")
    String username,

        @Schema(description = "User password", example = "1234")
        @NotBlank(message = "Password cannot be blank")
        @Size(max = 100, message = "First name must be at most 100 characters")
    String password,

    @Schema(description = "User email", example = "john.doe@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    String email,

    @Schema(description = "First name", example = "John")
    @Size(max = 100, message = "First name must be at most 100 characters")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    String lastName,

    @Schema(description = "Phone number", example = "+12345678901")
    @Size(max = 20, message = "Phone must be at most 20 characters")
    String phone
) {}
