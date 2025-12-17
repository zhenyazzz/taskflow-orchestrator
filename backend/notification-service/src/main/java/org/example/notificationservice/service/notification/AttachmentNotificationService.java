package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.attachment.AttachmentAddedEvent;
import org.example.events.attachment.AttachmentDeletedEvent;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.example.notificationservice.mapper.AttachmentNotificationMapper;
import org.example.notificationservice.dto.response.UserResponse;
import org.example.notificationservice.model.NotificationType;
@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentNotificationService {

    private final WebSocketDelivery webSocketDelivery;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final AttachmentNotificationMapper attachmentNotificationMapper;

    @Transactional
    public void handleAttachmentAdded(AttachmentAddedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.uploadedBy());
        String message = String.format("New attachment '%s' added to task '%s' by %s.",
                event.fileName(), event.taskId(), user.firstName());

        notificationRepository.save(
            attachmentNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.ATTACHMENT_ADDED.name(), event);
    }

    @Transactional
    public void handleAttachmentDeleted(AttachmentDeletedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.deletedBy());
        String message = String.format("Attachment '%s' deleted from task '%s' by %s.",
                event.fileName(), event.taskId(), user.firstName());

        notificationRepository.save(
            attachmentNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.ATTACHMENT_DELETED.name(), event);
    }
}
