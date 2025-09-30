package org.example.authservice.dto;

import lombok.Builder;
import org.example.events.enums.Role;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
public record JwtResponse(
    String token,
    UUID id,
    String username,
    Set<Role> roles
) {}

  
