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
        attachmentNotificationService.handleAttachmentAdded(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.attachment-deleted}", groupId = "notification-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeAttachmentDeleted(AttachmentDeletedEvent event) {
        log.info("Received AttachmentDeletedEvent: {}", event);
        attachmentNotificationService.handleAttachmentDeleted(event);
    }

}
