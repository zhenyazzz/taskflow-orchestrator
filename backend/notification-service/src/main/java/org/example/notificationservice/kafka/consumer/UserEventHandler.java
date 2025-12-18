package org.example.notificationservice.kafka.consumer;

import org.example.events.user.*;
import org.example.notificationservice.service.notification.UserNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventHandler {

    private final UserNotificationService userNotificationService;

    @KafkaListener(topics = "${app.kafka.topics.user-created}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserCreated(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent: {}", event);
        try {
            userNotificationService.handleUserCreated(event);
            log.debug("Successfully processed UserCreatedEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing UserCreatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegistered(UserRegistrationEvent event) {
        log.info("Received UserRegistrationEvent: {}", event);
        try {
            userNotificationService.handleUserRegistered(event);
            log.debug("Successfully processed UserRegistrationEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing UserRegistrationEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-profile-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Received UserProfileUpdatedEvent: {}", event);
        try {
            userNotificationService.handleUserProfileUpdated(event);
            log.debug("Successfully processed UserProfileUpdatedEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing UserProfileUpdatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserDeleted(UserDeletedEvent event) {
        log.info("Received UserDeletedEvent: {}", event);
        try {
            userNotificationService.handleUserDeleted(event);
            log.debug("Successfully processed UserDeletedEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing UserDeletedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-role-update}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Received UserRoleUpdateEvent: {}", event);
        try {
            userNotificationService.handleUserRoleUpdate(event);
            log.debug("Successfully processed UserRoleUpdateEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing UserRoleUpdateEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-login}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserLogin(UserLoginEvent event) {
        log.info("Received UserLoginEvent: {}", event);
        try {
            userNotificationService.handleUserLogin(event);
            log.debug("Successfully processed UserLoginEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing UserLoginEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-login-failed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeLoginFail(LoginFailEvent event) {
        log.info("Received LoginFailEvent: {}", event);
        try {
            userNotificationService.handleLoginFail(event);
            log.debug("Successfully processed LoginFailEvent for user: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing LoginFailEvent: {}", event, e);
            throw e;
        }
    }
}
