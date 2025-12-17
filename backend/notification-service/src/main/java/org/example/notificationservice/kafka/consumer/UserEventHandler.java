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
        userNotificationService.handleUserCreated(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegistered(UserRegistrationEvent event) {
        log.info("Received UserRegistrationEvent: {}", event);
        userNotificationService.handleUserRegistered(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-profile-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Received UserProfileUpdatedEvent: {}", event);
        userNotificationService.handleUserProfileUpdated(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserDeleted(UserDeletedEvent event) {
        log.info("Received UserDeletedEvent: {}", event);
        userNotificationService.handleUserDeleted(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-role-update}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Received UserRoleUpdateEvent: {}", event);
        userNotificationService.handleUserRoleUpdate(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-login}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserLogin(UserLoginEvent event) {
        log.info("Received UserLoginEvent: {}", event);
        userNotificationService.handleUserLogin(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-login-failed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeLoginFail(LoginFailEvent event) {
        log.info("Received LoginFailEvent: {}", event);
        userNotificationService.handleLoginFail(event);
    }
}
