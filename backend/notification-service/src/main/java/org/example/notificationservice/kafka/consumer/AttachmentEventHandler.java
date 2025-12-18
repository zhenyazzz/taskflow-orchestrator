package org.example.notificationservice.kafka.consumer;


import org.example.events.attachment.AttachmentAddedEvent;
import org.example.events.attachment.AttachmentDeletedEvent;
import org.example.notificationservice.service.notification.AttachmentNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentEventHandler {

    private final AttachmentNotificationService attachmentNotificationService;

    @KafkaListener(topics = "${app.kafka.topics.attachment-added}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAttachmentAdded(AttachmentAddedEvent event) {
        log.info("Received AttachmentAddedEvent: {}", event);
        try {
            attachmentNotificationService.handleAttachmentAdded(event);
            log.debug("Successfully processed AttachmentAddedEvent for task: {}", event.taskId());
        } catch (Exception e) {
            log.error("Error processing AttachmentAddedEvent: {}", event, e);
            throw e;
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.attachment-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAttachmentDeleted(AttachmentDeletedEvent event) {
        log.info("Received AttachmentDeletedEvent: {}", event);
        try {
            attachmentNotificationService.handleAttachmentDeleted(event);
            log.debug("Successfully processed AttachmentDeletedEvent for task: {}", event.taskId());
        } catch (Exception e) {
            log.error("Error processing AttachmentDeletedEvent: {}", event, e);
            throw e;
        }
    }

}
