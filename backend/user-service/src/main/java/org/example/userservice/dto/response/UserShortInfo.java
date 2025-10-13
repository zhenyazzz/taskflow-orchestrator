package org.example.userservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Short user info")
public record UserShortInfo(
        @Schema(description = "User id", example = "1")
        UUID id,
        @Schema(description = "Username", example = "john.doe")
        String username,
        @Schema(description = "Email", example = "john.doe@example.com")
        String email
) {
}
