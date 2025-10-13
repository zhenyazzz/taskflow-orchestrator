package org.example.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.events.enums.Role;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Current user profile model")
public record ProfileResponse(
        @Schema(description = "User id")
        UUID id,
        @Schema(description = "Username", example = "john.doe")
        String username,
        @Schema(description = "Email", example = "john.doe@example.com")
        String email,
        @Schema(description = "First name", example = "John")
        String firstName,
        @Schema(description = "Last name", example = "Doe")
        String lastName,
        @Schema(description = "Assigned roles")
        Set<Role> roles
) {
    
}
