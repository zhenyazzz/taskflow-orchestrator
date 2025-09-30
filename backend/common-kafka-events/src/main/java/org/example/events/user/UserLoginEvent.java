package org.example.events.user;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

import org.example.events.enums.Role;

@Builder
public record UserLoginEvent(
    UUID id,
    String username,
    String email,
    Set<Role> roles,
    String userAgent
) {

}
