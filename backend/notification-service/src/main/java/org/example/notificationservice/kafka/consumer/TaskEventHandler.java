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
        try {
            taskNotificationService.handleTaskCreated(event);
            log.debug("Successfully processed TaskCreatedEvent for task: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing TaskCreatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskUpdated(TaskUpdatedEvent event) {
        log.info("Received TaskUpdatedEvent: {}", event);
        try {
            taskNotificationService.handleTaskUpdated(event);
            log.debug("Successfully processed TaskUpdatedEvent for task: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing TaskUpdatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-subscribed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskSubscribed(TaskSubscribedEvent event) {
        log.info("Received TaskSubscribedEvent: {}", event);
        try {
            taskNotificationService.handleTaskSubscribed(event);
            log.debug("Successfully processed TaskSubscribedEvent for task: {} and user: {}", event.id(), event.userId());
        } catch (Exception e) {
            log.error("Error processing TaskSubscribedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-unsubscribed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskUnsubscribed(TaskUnsubscribedEvent event) {
        log.info("Received TaskUnsubscribedEvent: {}", event);
        try {
            taskNotificationService.handleTaskUnsubscribed(event);
            log.debug("Successfully processed TaskUnsubscribedEvent for task: {} and user: {}", event.id(), event.userId());
        } catch (Exception e) {
            log.error("Error processing TaskUnsubscribedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-completed}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskCompleted(TaskCompletedEvent event) {
        log.info("Received TaskCompletedEvent: {}", event);
        try {
            taskNotificationService.handleTaskCompleted(event);
            log.debug("Successfully processed TaskCompletedEvent for task: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing TaskCompletedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskDeleted(TaskDeletedEvent event) {
        log.info("Received TaskDeletedEvent: {}", event);
        try {
            taskNotificationService.handleTaskDeleted(event);
            log.debug("Successfully processed TaskDeletedEvent for task: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing TaskDeletedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-status-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Received TaskStatusUpdatedEvent: {}", event);
        try {
            taskNotificationService.handleTaskStatusUpdated(event);
            log.debug("Successfully processed TaskStatusUpdatedEvent for task: {} and user: {}", event.id(), event.userId());
        } catch (Exception e) {
            log.error("Error processing TaskStatusUpdatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-assignees-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Received TaskAssigneesUpdatedEvent: {}", event);
        try {
            taskNotificationService.handleTaskAssigneesUpdated(event);
            log.debug("Successfully processed TaskAssigneesUpdatedEvent for task: {} and user: {}", event.id(), event.assigneeIds());
        } catch (Exception e) {
            log.error("Error processing TaskAssigneesUpdatedEvent: {}", event, e);
            throw e;
        }
    }
}
