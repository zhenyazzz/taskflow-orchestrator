package org.example.notificationservice.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.task.*;
import org.example.notificationservice.client.UserServiceClient;
import org.example.notificationservice.repository.NotificationRepository;
import org.example.notificationservice.service.delivery.EmailDelivery;
import org.example.notificationservice.service.delivery.WebSocketDelivery;
import org.springframework.stereotype.Service;
import org.example.notificationservice.dto.response.UserResponse;
import org.example.notificationservice.mapper.TaskNotificationMapper;
import org.example.notificationservice.model.Notification;
import org.example.notificationservice.model.NotificationType;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
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


    @Transactional
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Handling TaskCreatedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("New task '%s' created.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> {
                    return taskNotificationMapper.toNotification(user, event, message);
                })
                .toList();
        notificationRepository.saveAll(notifications);

        users.forEach(user -> {
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_CREATED.name(), event);
            emailDelivery.sendEmail(user.email(), "New Task Created", message);
        });
    }

    @Transactional
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        log.info("Handling TaskUpdatedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("You updated task '%s'.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> {
                    return taskNotificationMapper.toNotification(user, event, message);
                })
                .toList();
        notificationRepository.saveAll(notifications);

        users.forEach(user -> {
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_UPDATED.name(), event);
            emailDelivery.sendEmail(user.email(), "Task Updated", message);
        });
    }

    @Transactional
    public void handleTaskSubscribed(TaskSubscribedEvent event) {
        log.info("Handling TaskSubscribedEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("You subscribed to task '%s'.", event.title());

        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_SUBSCRIBED.name(), event);
        emailDelivery.sendEmail(user.email(), "Task Subscription Notification", message);
    }

    @Transactional
    public void handleTaskUnsubscribed(TaskUnsubscribedEvent event) {
        log.info("Handling TaskUnsubscribedEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("You unsubscribed from task '%s'.", event.title());

        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_UNSUBSCRIBED.name(), event);
        emailDelivery.sendEmail(user.email(), "Task Unsubscription Notification", message);
    }

    @Transactional
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Handling TaskCompletedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("You completed task '%s'.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> {
                    return taskNotificationMapper.toNotification(user, event, message);
                })
                .toList();
        notificationRepository.saveAll(notifications);

        users.forEach(user -> {
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_COMPLETED.name(), event);
            emailDelivery.sendEmail(user.email(), "Task Completed", message);
        });
    }

    @Transactional
    public void handleTaskDeleted(TaskDeletedEvent event) {
        log.info("Handling TaskDeletedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("You deleted task '%s'.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> {
                    return taskNotificationMapper.toNotification(user, event, message);
                })
                .toList();
        notificationRepository.saveAll(notifications);

        users.forEach(user -> {
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_DELETED.name(), event);
            emailDelivery.sendEmail(user.email(), "Task Deleted", message);
        });
    }

    @Transactional
    public void handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Handling TaskStatusUpdatedEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.id());
        String message = String.format("You updated task '%s' status to '%s'.", event.title(), event.status());

        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_STATUS_UPDATED.name(), event);
        emailDelivery.sendEmail(user.email(), "Task Status Updated", message);
    }

    @Transactional
    public void handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Handling TaskAssigneesUpdatedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("You updated task '%s' assignees to '%s'.", event.title(), event.assigneeIds());

        List<Notification> notifications = users.stream()
                .map(user -> {
                    return taskNotificationMapper.toNotification(user, event, message);
                })
                .toList();
        notificationRepository.saveAll(notifications);

        users.forEach(user -> {
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_ASSIGNEE_UPDATED.name(), event);
            emailDelivery.sendEmail(user.email(), "Task Assignees Updated", message);
        });
    }

}
