package org.example.notificationservice.mapper;

import org.mapstruct.Mapper;
import org.example.notificationservice.model.Notification;
import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserRegistrationEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.example.events.user.UserDeletedEvent;
import org.example.events.user.UserRoleUpdateEvent;
import org.example.events.user.UserLoginEvent;
import org.example.events.user.LoginFailEvent;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface UserNotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "USER_CREATED")
    @Mapping(target = "metadata", expression = "java(mapUserCreatedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserCreatedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "USER_REGISTRATION")
    @Mapping(target = "metadata", expression = "java(mapUserRegistrationEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserRegistrationEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "USER_PROFILE_UPDATED")
    @Mapping(target = "metadata", expression = "java(mapUserProfileUpdatedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserProfileUpdatedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "USER_DELETED")
    @Mapping(target = "metadata", expression = "java(mapUserDeletedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDeletedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "USER_ROLE_UPDATE")
    @Mapping(target = "metadata", expression = "java(mapUserRoleUpdateEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserRoleUpdateEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "USER_LOGIN")
    @Mapping(target = "metadata", expression = "java(mapUserLoginEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserLoginEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", expression = "java(event.id().toString())")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "LOGIN_FAIL")
    @Mapping(target = "metadata", expression = "java(mapLoginFailEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(LoginFailEvent event, String message);

    @Named("mapUserCreatedEventMetadata")
    default Map<String, String> mapUserCreatedEventMetadata(UserCreatedEvent event) {
        return Map.of(
                "userId", event.id().toString(),
                "username", event.username(),
                "email", event.email()
        );
    }

    @Named("mapUserRegistrationEventMetadata")
    default Map<String, String> mapUserRegistrationEventMetadata(UserRegistrationEvent event) {
        return Map.of(
                "userId", event.id().toString(),
                "username", event.username(),
                "email", event.email()
        );
    }

    @Named("mapUserProfileUpdatedEventMetadata")
    default Map<String, String> mapUserProfileUpdatedEventMetadata(UserProfileUpdatedEvent event) {
        return Map.of(
                "userId", event.id().toString(),
                "username", event.username() != null ? event.username() : "N/A",
                "email", event.email() != null ? event.email() : "N/A"
        );
    }

    @Named("mapUserDeletedEventMetadata")
    default Map<String, String> mapUserDeletedEventMetadata(UserDeletedEvent event) {
        return Map.of(
                "userId", event.id().toString(),
                "username", event.username(),
                "email", event.email()
        );
    }

    @Named("mapUserRoleUpdateEventMetadata")
    default Map<String, String> mapUserRoleUpdateEventMetadata(UserRoleUpdateEvent event) {
        return Map.of(
                "userId", event.id().toString(),
                "role", event.role().name(),
                "action", event.action().name()
        );
    }

    @Named("mapUserLoginEventMetadata")
    default Map<String, String> mapUserLoginEventMetadata(UserLoginEvent event) {
        return Map.of(
                "userId", event.id().toString(),
                "username", event.username(),
                "email", event.email()
        );
    }

    @Named("mapLoginFailEventMetadata")
    default Map<String, String> mapLoginFailEventMetadata(LoginFailEvent event) {
        return Map.of(
                "userId", event.id() != null ? event.id() : "N/A",
                "username", event.username() != null ? event.username() : "N/A",
                "email", event.email() != null ? event.email() : "N/A",
                "failureReason", event.failureReason() != null ? event.failureReason() : "Unknown"
        );
    }
}
