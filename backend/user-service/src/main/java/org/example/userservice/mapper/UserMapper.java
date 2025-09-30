package org.example.userservice.mapper;

import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserDeletedEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.example.events.user.UserRegistrationEvent;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.ProfileResponse;
import org.example.userservice.dto.response.UserResponse;

import org.example.userservice.dto.response.UserShortInfo;
import org.example.userservice.model.User;
import org.mapstruct.*;

import java.time.Instant;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Instant.class, Collectors.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    User toUser(CreateUserRequest request);

    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    User toUser(UserRegistrationEvent event);

    void updateUserFromRequest(UpdateUserRequest request, @MappingTarget User user);

    UserResponse toUserResponse(User user);

    ProfileResponse toProfileResponse(User user);

    UserShortInfo toUserShortInfo(User user);

    @Mapping(target = "password", source = "password")
    UserCreatedEvent toUserCreatedEvent(User user, String password);

    @Mapping(target = "deletedAt", expression = "java(Instant.now())")
    UserDeletedEvent toUserDeletedEvent(User user);

    @Mapping(target = "password", source = "password")
    UserProfileUpdatedEvent toUserProfileUpdatedEvent(User user, String password);

} 