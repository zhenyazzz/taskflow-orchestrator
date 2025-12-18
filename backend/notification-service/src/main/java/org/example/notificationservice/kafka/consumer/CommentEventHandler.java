package org.example.notificationservice.kafka.consumer;

import org.example.events.comment.*;
import org.example.notificationservice.service.notification.CommentNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventHandler {

    private final CommentNotificationService commentNotificationService;

    @KafkaListener(topics = "${app.kafka.topics.comment-created}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommentCreated(CommentCreatedEvent event) {
        log.info("Received CommentCreatedEvent: {}", event);
        try {
            commentNotificationService.handleCommentCreated(event);
            log.debug("Successfully processed CommentCreatedEvent for task: {}", event.taskId());
        } catch (Exception e) {
            log.error("Error processing CommentCreatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.comment-updated}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommentUpdated(CommentUpdatedEvent event) {
        log.info("Received CommentUpdatedEvent: {}", event);
        try {
            commentNotificationService.handleCommentUpdated(event);
            log.debug("Successfully processed CommentUpdatedEvent for task: {}", event.taskId());
        } catch (Exception e) {
            log.error("Error processing CommentUpdatedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.comment-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommentDeleted(CommentDeletedEvent event) {
        log.info("Received CommentDeletedEvent: {}", event);
        try {
            commentNotificationService.handleCommentDeleted(event);
            log.debug("Successfully processed CommentDeletedEvent for task: {}", event.taskId());
        } catch (Exception e) {
            log.error("Error processing CommentDeletedEvent: {}", event, e);
            throw e;
        }
    }
}
