package org.example.events.user;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.example.events.enums.Role;

public record UserCreatedEvent(
    UUID id,
    String username,
    String password,
    String email,
    String firstName,
    String lastName,
    Set<Role> roles,
    Instant createdAt
) {

} 