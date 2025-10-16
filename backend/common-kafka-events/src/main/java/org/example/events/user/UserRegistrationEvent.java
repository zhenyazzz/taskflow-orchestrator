package org.example.events.user;
import java.util.Set;
import java.util.UUID;

import org.example.events.enums.Role;

public record UserRegistrationEvent(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    Set<Role> roles
) {

}
