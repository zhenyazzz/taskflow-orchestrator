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

    @KafkaListener(topics = "${kafka.topics.user-created}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserCreated(UserCreatedEvent event) {
        log.info("Получено событие UserCreatedEvent: {}", event);
        authService.handleUserCreation(event);
    }

    @KafkaListener(topics = "${kafka.topics.user-deleted}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserDeleted(UserDeletedEvent event) {
        log.info("Получено событие UserDeletedEvent: {}", event);
        authService.handleUserDelete(event);
    }
    @KafkaListener(topics = "${kafka.topics.user-profile-updated}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserProfileUpdated(UserProfileUpdatedEvent event) {
        log.info("Получено событие UserProfileUpdatedEvent: {}", event);
        authService.handleUserProfileUpdate(event);
    }
    
}
