package org.example.analyticsservice.kafka;
    
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.analyticsservice.service.AnalyticsService;
import org.example.events.task.*;
import org.example.events.user.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AnalyticsService analyticsService;
    
    // Task Events
    @KafkaListener(topics = "#{kafka.topics.task-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskCreated(TaskCreatedEvent event) {
        log.info("Received TaskCreatedEvent: {}", event);
            log.info("Получено событие TaskCreatedEvent для задачи: {}", event.id());
            analyticsService.handleTaskCreated(event);
    }

    @KafkaListener(topics = "#{kafka.topics.task-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskUpdated(TaskUpdatedEvent event) {
        log.info("Received TaskUpdatedEvent for task: {}", event.id());
        analyticsService.handleTaskUpdated(event);
    }

    @KafkaListener(topics = "#{kafka.topics.task-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskCompleted(TaskCompletedEvent event) {
        log.info("Received TaskCompletedEvent for task: {}", event.id());
        analyticsService.handleTaskCompleted(event);
    }

    @KafkaListener(topics = "#{kafka.topics.task-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskDeleted(TaskDeletedEvent event) {
        log.info("Received TaskDeletedEvent for task: {}", event.id());
        analyticsService.handleTaskDeleted(event);
    }

    // User Events
    @KafkaListener(topics = "#{kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegistered(UserCreatedEvent event) {
        log.info("Received UserCreatedEvent for user: {}", event.id());
        analyticsService.handleUserRegistered(event);
    }

    @KafkaListener(topics = "#{kafka.topics.user-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdated(UserProfileUpdatedEvent event) {
        log.info("Received UserProfileUpdatedEvent for user: {}", event.id());
        analyticsService.handleUserUpdated(event);
    }

    // Auth Events
    @KafkaListener(topics = "#{kafka.topics.user-login-success}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLoginSuccess(UserLoginEvent event) {
        log.info("Received UserLoginEvent (successful) for user: {}", event.id());
        analyticsService.handleUserLoginSuccess(event);
    }

    @KafkaListener(topics = "#{kafka.topics.user-login-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLoginFailed(LoginFailEvent loginFailEvent) {
        log.info("Received failed login event: {}", loginFailEvent.failureReason());
        analyticsService.handleUserLoginFailed(loginFailEvent);
    }

    // Дополнительные события задач
    @KafkaListener(topics = "#{kafka.topics.task-status-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Received TaskStatusUpdatedEvent for task: {}", event.id());
        analyticsService.handleTaskStatusUpdated(event);
    }

    @KafkaListener(topics = "#{kafka.topics.task-assignees-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Received TaskAssigneesUpdatedEvent for task: {}", event.id());
        analyticsService.handleTaskAssigneesUpdated(event);
    }
}
