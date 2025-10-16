package org.example.events.user;

import java.util.Set;
import java.util.UUID;

import org.example.events.enums.Role;


public record UserLoginEvent(
    UUID id,
    String username,
    String email,
    Set<Role> roles,
    String userAgent
) {

}
