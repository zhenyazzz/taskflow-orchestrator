package org.example.userservice.dto.response;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.events.enums.Role;
import org.example.events.enums.UserStatus;

@Schema(description = "User response model")
public record UserResponse(
        @Schema(description = "User id", example = "1")
        UUID id,
        @Schema(description = "Unique username", example = "john.doe")
        String username,
        @Schema(description = "User email", example = "john.doe@example.com")
        String email,
        @Schema(description = "First name", example = "John")
        String firstName,
        @Schema(description = "Last name", example = "Doe")
        String lastName,
        @Schema(description = "Phone", example = "+12345678901")
        String phone,
        @Schema(description = "Assigned roles")
        Set<Role> roles,
        @Schema(description = "User status")
        UserStatus status,
        @Schema(description = "Creation timestamp")
        Instant createdAt,
        @Schema(description = "Last update timestamp")
        Instant updatedAt
) {

}
