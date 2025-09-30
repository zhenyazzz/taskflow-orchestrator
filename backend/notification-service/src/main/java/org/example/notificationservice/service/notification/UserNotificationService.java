package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.user.*;
import org.example.notificationservice.service.delivery.EmailDelivery;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.example.notificationservice.mapper.UserNotificationMapper;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.model.Notification;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailDelivery emailDelivery;
    private final WebSocketDelivery webSocketDelivery;
    private final UserNotificationMapper userNotificationMapper;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;

    public Mono<Void> handleUserCreated(UserCreatedEvent event) {
        log.info("Handling UserCreatedEvent: {}", event);
        String message = String.format("New user '%s' with email '%s' has been created.",
                event.username(), event.email());

        Mono<Notification> saveNotification = notificationRepository.save(
            userNotificationMapper.toNotification(event, "USER_CREATED", message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-created", event);

        Mono<Void> emailMono = emailDelivery.sendEmail(event.email(), "Welcome to TaskFlow!", message);

        return Mono.when(saveNotification, webSocketMono, emailMono)
            .doOnSuccess(unused -> log.info("User created notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process user created event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing UserCreatedEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleUserRegistered(UserRegistrationEvent event) {
        log.info("Handling UserRegistrationEvent: {}", event);
        String message = String.format("User '%s' has successfully registered.", event.username());

        Mono<Notification> saveNotification = notificationRepository.save(
            userNotificationMapper.toNotification(event, "USER_REGISTERED", message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-registered", event);

        Mono<Void> emailMono = emailDelivery.sendEmail(event.email(), "Registration Successful!", message);

        return Mono.when(saveNotification, webSocketMono, emailMono)
            .doOnSuccess(unused -> log.info("User registration notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process user registration event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing UserRegistrationEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Handling UserProfileUpdatedEvent: {}", event);
        String message = String.format("Your profile information has been updated.", event.username());

        Mono<Notification> saveNotification = notificationRepository.save(
            userNotificationMapper.toNotification(event, "USER_PROFILE_UPDATED", message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-profile-updated", event);

        Mono<Void> emailMono = emailDelivery.sendEmail(event.email(), "Profile Updated!", message);

        return Mono.when(saveNotification, webSocketMono, emailMono)
            .doOnSuccess(unused -> log.info("User profile updated notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process user profile updated event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing UserProfileUpdatedEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleUserDeleted(UserDeletedEvent event) {
        log.info("Handling UserDeletedEvent: {}", event);
        String message = String.format("User '%s' has been deleted.", event.username());

        Mono<Notification> saveNotification = notificationRepository.save(
            userNotificationMapper.toNotification(event, "USER_DELETED", message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-deleted", event);

        Mono<Void> emailMono = emailDelivery.sendEmail(event.email(), "Account Deleted", message);

        return Mono.when(saveNotification, webSocketMono, emailMono)
            .doOnSuccess(unused -> log.info("User deleted notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process user deleted event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing UserDeletedEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }
    public Mono<Void> handleUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Handling UserRoleUpdateEvent: {}", event);
        return userServiceClient.getUserByIdAsync(event.id().toString())
            .flatMap(user -> {
                String message = String.format("User '%s' role has been updated to '%s'.", user.username(), event.role());

                Mono<Notification> saveNotification = notificationRepository.save(
                    userNotificationMapper.toNotification(event, "USER_ROLE_UPDATED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-role-updated", event);

                Mono<Void> emailMono = emailDelivery.sendEmail(user.email(), "Role Updated!", message);

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .doOnSuccess(unused -> log.info("User role updated notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process user role update event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing UserRoleUpdateEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleUserLogin(UserLoginEvent event) {
        log.info("Handling UserLoginEvent: {}", event);
        String message = String.format("User '%s' has logged in successfully.", event.username());

        Mono<Notification> saveNotification = notificationRepository.save(
            userNotificationMapper.toNotification(event, "USER_LOGIN", message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-login", event);

        return Mono.when(saveNotification, webSocketMono)
            .doOnSuccess(unused -> log.info("User login notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process user login event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing UserLoginEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleLoginFail(LoginFailEvent event) {
        log.info("Handling LoginFailEvent: {}", event);
        String message = String.format("Failed login attempt for user '%s' from IP address '%s'.",
                event.username(), event.id()); // Using event.id() as IP address placeholder based on previous implementation

        Mono<Notification> saveNotification = notificationRepository.save(
            userNotificationMapper.toNotification(event, "LOGIN_FAILED", message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id(), "login-fail", event);

        return Mono.when(saveNotification, webSocketMono)
            .doOnSuccess(unused -> log.info("Login failed notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process login failed event for user {}: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing LoginFailEvent for user {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }
}
