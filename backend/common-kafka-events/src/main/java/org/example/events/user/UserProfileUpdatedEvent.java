package org.example.events.user;

import lombok.Builder;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.example.events.enums.Role;


@Builder
public record UserProfileUpdatedEvent(
    UUID id,
    String username,
    String password,
    String email,
    String firstName,
    String lastName,
    Set<Role> roles,
    Instant updatedAt
) {
} 