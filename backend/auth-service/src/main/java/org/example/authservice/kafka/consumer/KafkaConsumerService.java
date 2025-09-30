package org.example.authservice.kafka.consumer;


import org.example.authservice.service.AuthService;
import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserDeletedEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final AuthService authService;

    @KafkaListener(topics = "${app.kafka.topics.user-created}", groupId = "auth-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserCreate(UserCreatedEvent event) {
        log.info("Получено событие UserCreatedEvent: {}", event);
        authService.handleUserCreation(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-delete}", groupId = "auth-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserDelete(UserDeletedEvent event) {
        log.info("Получено событие UserDeletedEvent: {}", event);
        authService.handleUserDelete(event);
    }
    @KafkaListener(topics = "${app.kafka.topics.user-profile-update}", groupId = "auth-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserProfileUpdate(UserProfileUpdatedEvent event) {
        log.info("Получено событие UserProfileUpdatedEvent: {}", event);
        authService.handleUserProfileUpdate(event);
    }
    
}
