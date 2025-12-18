package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.user.*;
import org.example.notificationservice.dto.response.UserResponse;
import org.example.notificationservice.service.delivery.EmailDelivery;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import org.example.notificationservice.mapper.UserNotificationMapper;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.model.Notification;
import org.example.notificationservice.model.NotificationType;
import org.springframework.transaction.annotation.Transactional;
import org.example.notificationservice.mapper.NotificationMapper;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailDelivery emailDelivery;
    private final WebSocketDelivery webSocketDelivery;
    private final UserNotificationMapper userNotificationMapper;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Handling UserCreatedEvent: {}", event);
        String message = String.format("New user '%s' with email '%s' has been created.",
                event.username(), event.email());

        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), NotificationType.USER_CREATED.name(), notificationMapper.toDto(notification));

        emailDelivery.sendEmail(event.email(), "Welcome to TaskFlow!", message);

    }

    @Transactional
    public void handleUserRegistered(UserRegistrationEvent event) {
        log.info("Handling UserRegistrationEvent: {}", event);
        String message = String.format("User '%s' has successfully registered.", event.username());

        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), NotificationType.USER_REGISTRATION.name(), notificationMapper.toDto(notification));

        emailDelivery.sendEmail(event.email(), "Registration Successful!", message);

    }

    @Transactional
    public void handleUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Handling UserProfileUpdatedEvent: {}", event);
        String message = String.format("Your profile information has been updated.", event.username());

        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), NotificationType.USER_PROFILE_UPDATED.name(), notificationMapper.toDto(notification));
    }

    @Transactional
    public void handleUserDeleted(UserDeletedEvent event) {
        log.info("Handling UserDeletedEvent: {}", event);
        String message = String.format("User '%s' has been deleted.", event.username());

        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), NotificationType.USER_DELETED.name(), notificationMapper.toDto(notification));

        emailDelivery.sendEmail(event.email(), "Account Deleted", message);

    }
    @Transactional
    public void handleUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Handling UserRoleUpdateEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.id().toString());
        String message = String.format("Your role has been updated.", user.username());
        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.USER_ROLE_UPDATE.name(), notificationMapper.toDto(notification));

        emailDelivery.sendEmail(user.email(), "Role Updated!", message);

    }

    @Transactional
    public void handleUserLogin(UserLoginEvent event) {
        log.info("Handling UserLoginEvent: {}", event);
        String message = String.format("User '%s' has logged in successfully.", event.username());

        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id().toString(), NotificationType.USER_LOGIN.name(), notificationMapper.toDto(notification));

    }

    @Transactional
    public void handleLoginFail(LoginFailEvent event) {
        log.info("Handling LoginFailEvent: {}", event);
        String message = String.format("Failed login attempt for user '%s',userAgent: %s with reason: %s.", event.username(), event.userAgent(), event.failureReason());

        Notification notification = notificationRepository.save(
            userNotificationMapper.toNotification(event, message)
        );

        webSocketDelivery.sendWebSocketNotification(event.id(), NotificationType.LOGIN_FAIL.name(), notificationMapper.toDto(notification));
        
    }
}
