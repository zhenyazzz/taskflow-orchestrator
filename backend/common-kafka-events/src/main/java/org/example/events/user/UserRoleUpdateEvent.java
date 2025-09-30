package org.example.events.user;

import java.util.UUID;

import org.example.events.enums.Role;
import org.example.events.enums.RoleAction;

import lombok.Builder;

@Builder
public record UserRoleUpdateEvent(
    UUID id,
    Role role,
    RoleAction action
) {}