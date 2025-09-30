package org.example.authservice.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.example.authservice.dto.JwtResponse;
import org.example.authservice.dto.RegisterRequest;

import org.example.authservice.model.User;
import org.example.authservice.util.JwtUtil;
import org.example.events.enums.Role;
import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserLoginEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.example.events.user.UserRegistrationEvent;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "roles", expression = "java(java.util.Set.of(Role.ROLE_USER))")
    @Mapping(target = "id", ignore = true)
    User toUser(RegisterRequest registerRequest, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    User toUser(UserCreatedEvent event, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "id")
    @Mapping(target = "token", expression = "java(jwtUtil.generateTokenFromUser(user))")
    JwtResponse toJwtResponse(User user, @Context JwtUtil jwtUtil);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "phone", source = "phone")
    UserRegistrationEvent toUserRegistrationEvent(User user, String firstName, String lastName, String phone);

    UserLoginEvent toUserLoginEvent(User user, String userAgent);

    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    void updateUser(UserProfileUpdatedEvent event, @MappingTarget User user, @Context PasswordEncoder passwordEncoder);

    @Named("encodePassword")
    default String encodePassword(String rawPassword, @Context PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(rawPassword);
    }

}
