package org.example.events.user;

import lombok.Builder;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.example.events.enums.Role;


@Builder
public record UserDeletedEvent(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    Set<Role> roles,
    Instant deletedAt) {
}
