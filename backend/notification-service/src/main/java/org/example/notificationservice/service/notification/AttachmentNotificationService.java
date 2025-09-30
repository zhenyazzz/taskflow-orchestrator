package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.attachment.AttachmentAddedEvent;
import org.example.events.attachment.AttachmentDeletedEvent;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.service.delivery.EmailDelivery;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import org.example.notificationservice.model.Notification;
import org.example.notificationservice.mapper.AttachmentNotificationMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentNotificationService {

    private final EmailDelivery emailDelivery;
    private final WebSocketDelivery webSocketDelivery;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final AttachmentNotificationMapper attachmentNotificationMapper;

    public Mono<Void> handleAttachmentAdded(AttachmentAddedEvent event) {
        return userServiceClient.getUserByIdAsync(event.uploadedBy())
            .flatMap(user -> {
                String message = String.format("New attachment '%s' added to task '%s' by %s.",
                        event.fileName(), event.taskId(), user.firstName());

                Mono<Notification> saveNotification = notificationRepository.save(
                    attachmentNotificationMapper.toNotification(user, event, "ATTACHMENT_ADDED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "attachment-added", event);

                Mono<Void> emailMono = emailDelivery.sendEmail(user.email(), "New Attachment Added", message);

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .doOnSuccess(unused -> log.info("Attachment added notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process attachment added event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing AttachmentAddedEvent for attachment {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleAttachmentDeleted(AttachmentDeletedEvent event) {
        return userServiceClient.getUserByIdAsync(event.deletedBy())
            .flatMap(user -> {
                String message = String.format("Attachment '%s' deleted from task '%s' by %s.",
                        event.fileName(), event.taskId(), user.firstName());

                Mono<Notification> saveNotification = notificationRepository.save(
                    attachmentNotificationMapper.toNotification(user, event, "ATTACHMENT_DELETED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "attachment-deleted", event);

                return Mono.when(saveNotification, webSocketMono);
            })
            .doOnSuccess(unused -> log.info("Attachment deleted notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process attachment deleted event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing AttachmentDeletedEvent for attachment {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }
}
