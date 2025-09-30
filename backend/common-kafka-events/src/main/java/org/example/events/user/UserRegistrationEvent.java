package org.example.events.user;
import java.util.Set;
import java.util.UUID;

import org.example.events.enums.Role;

import lombok.Builder;

@Builder
public record UserRegistrationEvent(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    String phone,
    Set<Role> roles
) {

}
