package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.comment.*;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.mapper.CommentNotificationMapper;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import org.example.notificationservice.dto.response.UserResponse;
import org.springframework.transaction.annotation.Transactional;
import org.example.notificationservice.model.Notification;
import org.example.notificationservice.model.NotificationType;
import org.example.notificationservice.mapper.NotificationMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentNotificationService {

    private final WebSocketDelivery webSocketDelivery;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final CommentNotificationMapper commentNotificationMapper;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void handleCommentCreated(CommentCreatedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.authorId());
        String message = String.format("New comment added to task '%s' by %s: %s",
                event.taskId(), user.firstName(), event.content());

        Notification notification = notificationRepository.save(
            commentNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.COMMENT_CREATED.name(), notificationMapper.toDto(notification));
    }

    @Transactional
    public void handleCommentUpdated(CommentUpdatedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.authorId());
        String message = String.format("Comment '%s' for task '%s' updated by %s: %s",
                event.id(), event.taskId(), user.firstName(), event.content());

        Notification notification = notificationRepository.save(
            commentNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.COMMENT_UPDATED.name(), notificationMapper.toDto(notification));
    }

    @Transactional
    public void handleCommentDeleted(CommentDeletedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.authorId());
        String message = String.format("Comment '%s' for task '%s' deleted by %s.",
                event.id(), event.taskId(), user.firstName());

        Notification notification = notificationRepository.save(
            commentNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.COMMENT_DELETED.name(), notificationMapper.toDto(notification));
    }
}
