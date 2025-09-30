package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.comment.*;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.mapper.CommentNotificationMapper;
import org.example.notificationservice.service.delivery.EmailDelivery;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.example.notificationservice.model.Notification;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentNotificationService {

    private final EmailDelivery emailDelivery;
    private final WebSocketDelivery webSocketDelivery;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final CommentNotificationMapper commentNotificationMapper;

    public Mono<Void> handleCommentCreated(CommentCreatedEvent event) {
        return userServiceClient.getUserByIdAsync(event.authorId())
            .flatMap(user -> {
                String message = String.format("New comment added to task '%s' by %s: %s",
                        event.taskId(), user.firstName(), event.content());

                Mono<Notification> saveNotification = notificationRepository.save(
                    commentNotificationMapper.toNotification(user, event, "COMMENT_CREATED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "comment-created", event);

                Mono<Void> emailMono = emailDelivery.sendEmail(user.email(), "New Comment Notification", message);

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .doOnSuccess(unused -> log.info("Comment created notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process comment created event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing CommentCreatedEvent for comment {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleCommentUpdated(CommentUpdatedEvent event) {
        return userServiceClient.getUserByIdAsync(event.authorId())
            .flatMap(user -> {
                String message = String.format("Comment '%s' for task '%s' updated by %s: %s",
                        event.id(), event.taskId(), user.firstName(), event.content());

                Mono<Notification> saveNotification = notificationRepository.save(
                    commentNotificationMapper.toNotification(user, event, "COMMENT_UPDATED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "comment-updated", event);

                return Mono.when(saveNotification, webSocketMono);
            })
            .doOnSuccess(unused -> log.info("Comment updated notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process comment updated event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing CommentUpdatedEvent for comment {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleCommentDeleted(CommentDeletedEvent event) {
        return userServiceClient.getUserByIdAsync(event.authorId())
            .flatMap(user -> {
                String message = String.format("Comment '%s' for task '%s' deleted by %s.",
                        event.id(), event.taskId(), user.firstName());

                Mono<Notification> saveNotification = notificationRepository.save(
                    commentNotificationMapper.toNotification(user, event, "COMMENT_DELETED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "comment-deleted", event);

                return Mono.when(saveNotification, webSocketMono);
            })
            .doOnSuccess(unused -> log.info("Comment deleted notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process comment deleted event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing CommentDeletedEvent for comment {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }
}
