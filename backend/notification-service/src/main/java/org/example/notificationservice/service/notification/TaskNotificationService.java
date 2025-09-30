package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.task.*;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.service.delivery.EmailDelivery;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.example.notificationservice.model.Notification;
import org.example.notificationservice.mapper.TaskNotificationMapper;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
public class TaskNotificationService {

    private final EmailDelivery emailDelivery;
    private final WebSocketDelivery webSocketDelivery;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final TaskNotificationMapper taskNotificationMapper;


    public Mono<Void> handleTaskCreated(TaskCreatedEvent event) {
        return userServiceClient.getUserByIdAsync(event.creatorId())
            .flatMap(creator -> {
                String creatorFirstName = creator.firstName();
                
                Set<String> recipientIds = new HashSet<>(event.assigneeIds());
                recipientIds.add(event.creatorId());

                return Flux.fromIterable(recipientIds)
                    .flatMap(userId -> processTaskCreatedNotificationForUser(userId, creatorFirstName, event))
                    .then()
                    .doOnSuccess(unused -> log.info("Task created notification processed for event: {}", event.id()))
                    .doOnError(error -> log.error("Failed to process task created event: {}", event.id(), error))
                    .onErrorResume(e -> {
                        log.error("Error processing TaskCreatedEvent for task {}: {}", event.id(), e.getMessage());
                        return Mono.empty();
                    });
            });
    }

    public Mono<Void> handleTaskUpdated(TaskUpdatedEvent event) {
        return userServiceClient.getUserByIdAsync(event.creatorId())
            .flatMap(creator -> {
                String creatorFirstName = creator.firstName();

                Set<String> recipientIds = new HashSet<>(event.assigneeIds());
                recipientIds.add(event.creatorId());

                return Flux.fromIterable(recipientIds)
                    .flatMap(userId -> processTaskUpdatedNotificationForUser(userId, creatorFirstName, event))
                    .then()
                    .doOnSuccess(unused -> log.info("Task updated notification processed for event: {}", event.id()))
                    .doOnError(error -> log.error("Failed to process task updated event: {}", event.id(), error))
                    .onErrorResume(e -> {
                        log.error("Error processing TaskUpdatedEvent for task {}: {}", event.id(), e.getMessage());
                        return Mono.empty();
                    });
            });
    }

    public Mono<Void> handleTaskSubscribed(TaskSubscribedEvent event) {
        return userServiceClient.getUserByIdAsync(event.userId())
            .flatMap(user -> {
                String message = String.format("%s subscribed to task '%s'.",
                        user.firstName(), event.title());

                Mono<Notification> saveNotification = notificationRepository.save(
                    taskNotificationMapper.toNotification(user, event, "TASK_SUBSCRIBED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "task-subscribed", event);

                Mono<Void> emailMono = emailDelivery.sendEmail(user.email(), "Task Subscription Notification", message);

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .doOnSuccess(unused -> log.info("Task subscribed notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process task subscribed event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing TaskSubscribedEvent for task {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleTaskUnsubscribed(TaskUnsubscribedEvent event) {
        return userServiceClient.getUserByIdAsync(event.userId())
            .flatMap(user -> {
                String message = String.format("%s unsubscribed from task '%s'.",
                        user.firstName(), event.title());

                Mono<Notification> saveNotification = notificationRepository.save(
                    taskNotificationMapper.toNotification(user, event, "TASK_UNSUBSCRIBED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(user.id().toString(), "task-unsubscribed", event);

                return Mono.when(saveNotification, webSocketMono);
            })
            .doOnSuccess(unused -> log.info("Task unsubscribed notification processed for event: {}", event.id()))
            .doOnError(error -> log.error("Failed to process task unsubscribed event: {}", event.id(), error))
            .onErrorResume(e -> {
                log.error("Error processing TaskUnsubscribedEvent for task {}: {}", event.id(), e.getMessage());
                return Mono.empty();
            });
    }

    public Mono<Void> handleTaskCompleted(TaskCompletedEvent event) {
        return userServiceClient.getUserByIdAsync(event.userId())
            .flatMap(completer -> {
                String completerFirstName = completer.firstName();

                Set<String> recipientIds = new HashSet<>(event.assigneeIds());
                recipientIds.add(event.userId());

                return Flux.fromIterable(recipientIds)
                    .flatMap(userId -> processTaskCompletedNotificationForUser(userId, completerFirstName, event))
                    .then()
                    .doOnSuccess(unused -> log.info("Task completed notification processed for event: {}", event.id()))
                    .doOnError(error -> log.error("Failed to process task completed event: {}", event.id(), error))
                    .onErrorResume(e -> {
                        log.error("Error processing TaskCompletedEvent for task {}: {}", event.id(), e.getMessage());
                        return Mono.empty();
                    });
            });
    }

    public Mono<Void> handleTaskDeleted(TaskDeletedEvent event) {
        return userServiceClient.getUserByIdAsync(event.userId())
            .flatMap(deleter -> {
                String deleterFirstName = deleter.firstName();

                Set<String> recipientIds = new HashSet<>(event.assigneeIds());
                recipientIds.add(event.creatorId());
                recipientIds.add(event.userId());

                return Flux.fromIterable(recipientIds)
                    .flatMap(userId -> processTaskDeletedNotificationForUser(userId, deleterFirstName, event))
                    .then()
                    .doOnSuccess(unused -> log.info("Task deleted notification processed for event: {}", event.id()))
                    .doOnError(error -> log.error("Failed to process task deleted event: {}", event.id(), error))
                    .onErrorResume(e -> {
                        log.error("Error processing TaskDeletedEvent for task {}: {}", event.id(), e.getMessage());
                        return Mono.empty();
                    });
            });
    }

    public Mono<Void> handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Handling TaskStatusUpdatedEvent: {}", event);
        return Mono.empty();
    }

    public Mono<Void> handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Handling TaskAssigneesUpdatedEvent: {}", event);
        return Mono.empty();
    }

    private Mono<Void> processTaskCreatedNotificationForUser(String userId, String creatorFirstName, TaskCreatedEvent event) {
        return userServiceClient.getUserByIdAsync(userId)
            .flatMap(user -> {
                String message;
                if (userId.equals(event.creatorId())) {
                    message = String.format("You created a new task '%s'.", event.title());
                } else {
                    message = String.format("New task '%s' created by %s and assigned to you.",
                            event.title(), creatorFirstName);
                }
    
                Mono<Notification> saveNotification = notificationRepository.save(
                    taskNotificationMapper.toNotification(user, event, "TASK_CREATED", message)
                );
    
                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(
                    user.id().toString(), "task-created", event
                );
    
                Mono<Void> emailMono = emailDelivery.sendEmail(
                    user.email(), "New Task Created", message
                );
    
                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .onErrorResume(e -> {
                log.warn("Failed to send notification to user {} for task {}: {}", 
                        userId, event.id(), e.getMessage());
                return Mono.empty(); 
            });
    }

    private Mono<Void> processTaskUpdatedNotificationForUser(String userId, String creatorFirstName, TaskUpdatedEvent event) {
        return userServiceClient.getUserByIdAsync(userId)
            .flatMap(user -> {
                String message;
                if (userId.equals(event.creatorId())) {
                    message = String.format("You updated task '%s'.", event.title());
                } else {
                    message = String.format("Task '%s' updated by %s.",
                            event.title(), creatorFirstName);
                }

                Mono<Notification> saveNotification = notificationRepository.save(
                    taskNotificationMapper.toNotification(user, event, "TASK_UPDATED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(
                    user.id().toString(), "task-updated", event
                );

                Mono<Void> emailMono = emailDelivery.sendEmail(
                    user.email(), "Task Updated", message
                );

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .onErrorResume(e -> {
                log.warn("Failed to send notification to user {} for task {}: {}", 
                        userId, event.id(), e.getMessage());
                return Mono.empty(); 
            });
    }

    private Mono<Void> processTaskCompletedNotificationForUser(String userId, String completerFirstName, TaskCompletedEvent event) {
        return userServiceClient.getUserByIdAsync(userId)
            .flatMap(user -> {
                String message;
                if (userId.equals(event.userId())) {
                    message = String.format("You completed task '%s'.", event.title());
                } else {
                    message = String.format("Task '%s' was completed by %s.",
                            event.title(), completerFirstName);
                }

                Mono<Notification> saveNotification = notificationRepository.save(
                    taskNotificationMapper.toNotification(user, event, "TASK_COMPLETED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(
                    user.id().toString(), "task-completed", event
                );

                Mono<Void> emailMono = emailDelivery.sendEmail(
                    user.email(), "Task Completed", message
                );

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .onErrorResume(e -> {
                log.warn("Failed to send notification to user {} for task {}: {}", 
                        userId, event.id(), e.getMessage());
                return Mono.empty(); 
            });
    }

    private Mono<Void> processTaskDeletedNotificationForUser(String userId, String deleterFirstName, TaskDeletedEvent event) {
        return userServiceClient.getUserByIdAsync(userId)
            .flatMap(user -> {
                String message;
                if (userId.equals(event.userId())) {
                    message = String.format("You deleted task '%s'.", event.title());
                } else if (userId.equals(event.creatorId())) {
                    message = String.format("Task '%s' created by you was deleted by %s.",
                            event.title(), deleterFirstName);
                } else {
                    message = String.format("Task '%s' assigned to you was deleted by %s.",
                            event.title(), deleterFirstName);
                }

                Mono<Notification> saveNotification = notificationRepository.save(
                    taskNotificationMapper.toNotification(user, event, "TASK_DELETED", message)
                );

                Mono<Void> webSocketMono = webSocketDelivery.sendWebSocketNotification(
                    user.id().toString(), "task-deleted", event
                );

                Mono<Void> emailMono = emailDelivery.sendEmail(
                    user.email(), "Task Deleted", message
                );

                return Mono.when(saveNotification, webSocketMono, emailMono);
            })
            .onErrorResume(e -> {
                log.warn("Failed to send notification to user {} for task {}: {}", 
                        userId, event.id(), e.getMessage());
                return Mono.empty(); 
            });
    }
}
