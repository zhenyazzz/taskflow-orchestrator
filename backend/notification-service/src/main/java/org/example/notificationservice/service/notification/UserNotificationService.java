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

    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Handling UserCreatedEvent: {}", event);
        String message = String.format("New user '%s' with email '%s' has been created.",
                event.username(), event.email());

        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-created", event);

        emailDelivery.sendEmail(event.email(), "Welcome to TaskFlow!", message);

    }

    public void handleUserRegistered(UserRegistrationEvent event) {
        log.info("Handling UserRegistrationEvent: {}", event);
        String message = String.format("User '%s' has successfully registered.", event.username());

        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-registered", event);

        emailDelivery.sendEmail(event.email(), "Registration Successful!", message);

    }

    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Handling UserProfileUpdatedEvent: {}", event);
        String message = String.format("Your profile information has been updated.", event.username());

        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-profile-updated", event);

    }

    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Handling UserDeletedEvent: {}", event);
        String message = String.format("User '%s' has been deleted.", event.username());

        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-deleted", event);

        emailDelivery.sendEmail(event.email(), "Account Deleted", message);

    }
    public void handleUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Handling UserRoleUpdateEvent: {}", event);
        String message = String.format("Your role has been updated.", event.username());
        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-role-updated", event);

        emailDelivery.sendEmail(event.email(), "Role Updated!", message);


    }

    public void handleUserLogin(UserLoginEvent event) {
        log.info("Handling UserLoginEvent: {}", event);
        String message = String.format("User '%s' has logged in successfully.", event.username());

        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id().toString(), "user-login", event);

    }

    public void handleLoginFail(LoginFailEvent event) {
        log.info("Handling LoginFailEvent: {}", event);
        String message = String.format("Failed login attempt for user '%s' from IP address '%s'.",
                event.username(), event.ipAddress());

        notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(event.id(), "login-fail", event);
        
    }
}
