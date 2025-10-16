package org.example.userservice.kafka.consumer;

import org.example.events.user.UserRegistrationEvent;
import org.example.events.user.UserRoleUpdateEvent;
import org.example.userservice.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final UserService userService;

    @KafkaListener(topics = "${kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegistered(UserRegistrationEvent event) {
        log.info("Получено событие UserRegistrationEvent: {}", event);
        userService.handleUserRegistration(event);
    }

    @KafkaListener(topics = "${kafka.topics.user-role-updated}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Получено событие UserRoleUpdateEvent: {}", event);
        userService.handleUserRoleUpdate(event);
    }
    
}

