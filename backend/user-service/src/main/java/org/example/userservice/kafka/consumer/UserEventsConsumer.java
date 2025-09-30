package org.example.userservice.kafka.consumer;


import org.example.events.user.UserRegistrationEvent;
import org.example.events.user.UserRoleUpdateEvent;
import org.example.userservice.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventsConsumer {
    private final UserService userService;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRegistration(UserRegistrationEvent event) {
        log.info("Получено событие UserRegistrationEvent: {}", event);
        userService.handleUserRegistration(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.user-role-update}", groupId = "user-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeUserRoleUpdate(UserRoleUpdateEvent event) {
        log.info("Получено событие UserRoleUpdateEvent: {}", event);
        userService.handleUserRoleUpdate(event);
    }
}
