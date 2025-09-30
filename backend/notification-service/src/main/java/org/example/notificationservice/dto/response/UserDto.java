package org.example.notificationservice.dto.response;

import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    String phone,
    String firstName,
    String lastName
) {
}
