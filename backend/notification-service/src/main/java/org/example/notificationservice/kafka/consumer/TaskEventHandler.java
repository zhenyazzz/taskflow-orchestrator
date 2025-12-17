package org.example.notificationservice.kafka.consumer;

import org.example.events.task.*;
import org.example.notificationservice.service.notification.TaskNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventHandler {

    private final TaskNotificationService taskNotificationService;

    @KafkaListener(topics = "${app.kafka.topics.task-created}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskCreated(TaskCreatedEvent event) {
        log.info("Received TaskCreatedEvent: {}", event);
        taskNotificationService.handleTaskCreated(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskUpdated(TaskUpdatedEvent event) {
        log.info("Received TaskUpdatedEvent: {}", event);
        taskNotificationService.handleTaskUpdated(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-subscribed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskSubscribed(TaskSubscribedEvent event) {
        log.info("Received TaskSubscribedEvent: {}", event);
        taskNotificationService.handleTaskSubscribed(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-unsubscribed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskUnsubscribed(TaskUnsubscribedEvent event) {
        log.info("Received TaskUnsubscribedEvent: {}", event);
        taskNotificationService.handleTaskUnsubscribed(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-completed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskCompleted(TaskCompletedEvent event) {
        log.info("Received TaskCompletedEvent: {}", event);
        taskNotificationService.handleTaskCompleted(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskDeleted(TaskDeletedEvent event) {
        log.info("Received TaskDeletedEvent: {}", event);
        taskNotificationService.handleTaskDeleted(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-status-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Received TaskStatusUpdatedEvent: {}", event);
        taskNotificationService.handleTaskStatusUpdated(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.task-assignees-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Received TaskAssigneesUpdatedEvent: {}", event);
        taskNotificationService.handleTaskAssigneesUpdated(event);
    }
}
