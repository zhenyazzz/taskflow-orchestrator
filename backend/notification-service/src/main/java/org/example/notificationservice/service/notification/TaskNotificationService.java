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
import org.example.notificationservice.mapper.NotificationMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class TaskNotificationService {

    private final EmailDelivery emailDelivery;
    private final WebSocketDelivery webSocketDelivery;
    private final UserServiceClient userServiceClient;
    private final NotificationRepository notificationRepository;
    private final TaskNotificationMapper taskNotificationMapper;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Handling TaskCreatedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("New task '%s' created.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> taskNotificationMapper.toNotification(user, event, message))
                .toList();

        Map<String, Notification> notificationMap = notificationRepository.saveAll(notifications).stream()
                .collect(Collectors.toMap(Notification::getUserId, n -> n));

        users.forEach(user -> {
            Notification notification = notificationMap.get(user.id());
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_CREATED.name(), notificationMapper.toDto(notification));
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
                .map(user -> taskNotificationMapper.toNotification(user, event, message))
                .toList();

        Map<String, Notification> notificationMap = notificationRepository.saveAll(notifications).stream()
                .collect(Collectors.toMap(Notification::getUserId, n -> n));

        users.forEach(user -> {
            Notification notification = notificationMap.get(user.id());
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_UPDATED.name(), notificationMapper.toDto(notification));
            emailDelivery.sendEmail(user.email(), "Task Updated", message);
        });
    }

    @Transactional
    public void handleTaskSubscribed(TaskSubscribedEvent event) {
        log.info("Handling TaskSubscribedEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("You subscribed to task '%s'.", event.title());

        Notification notification = notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_SUBSCRIBED.name(), notificationMapper.toDto(notification));
        emailDelivery.sendEmail(user.email(), "Task Subscription Notification", message);
    }

    @Transactional
    public void handleTaskUnsubscribed(TaskUnsubscribedEvent event) {
        log.info("Handling TaskUnsubscribedEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("You unsubscribed from task '%s'.", event.title());

        Notification notification = notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_UNSUBSCRIBED.name(), notificationMapper.toDto(notification));
        emailDelivery.sendEmail(user.email(), "Task Unsubscription Notification", message);
    }

    @Transactional
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Handling TaskCompletedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("Task '%s' has been completed.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> taskNotificationMapper.toNotification(user, event, message))
                .toList();

        Map<String, Notification> notificationMap = notificationRepository.saveAll(notifications).stream()
                .collect(Collectors.toMap(Notification::getUserId, n -> n));

        users.forEach(user -> {
            Notification notification = notificationMap.get(user.id());
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_COMPLETED.name(), notificationMapper.toDto(notification));
            emailDelivery.sendEmail(user.email(), "Task Completed", message);
        });
    }

    @Transactional
    public void handleTaskDeleted(TaskDeletedEvent event) {
        log.info("Handling TaskDeletedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("Task '%s' has been deleted.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> taskNotificationMapper.toNotification(user, event, message))
                .toList();

        Map<String, Notification> notificationMap = notificationRepository.saveAll(notifications).stream()
                .collect(Collectors.toMap(Notification::getUserId, n -> n));

        users.forEach(user -> {
            Notification notification = notificationMap.get(user.id());
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_DELETED.name(), notificationMapper.toDto(notification));
            emailDelivery.sendEmail(user.email(), "Task Deleted", message);
        });
    }

    @Transactional
    public void handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Handling TaskStatusUpdatedEvent: {}", event);
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("Task '%s' status updated to '%s'.", event.title(), event.status());

        Notification notification = notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_STATUS_UPDATED.name(), notificationMapper.toDto(notification));
        emailDelivery.sendEmail(user.email(), "Task Status Updated", message);
    }

    @Transactional
    public void handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Handling TaskAssigneesUpdatedEvent: {}", event);
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        List<UserResponse> users = userServiceClient.getUsersByIds(recipientIds);
        String message = String.format("Task '%s' assignees updated.", event.title());

        List<Notification> notifications = users.stream()
                .map(user -> taskNotificationMapper.toNotification(user, event, message))
                .toList();

        Map<String, Notification> notificationMap = notificationRepository.saveAll(notifications).stream()
                .collect(Collectors.toMap(Notification::getUserId, n -> n));

        users.forEach(user -> {
            Notification notification = notificationMap.get(user.id());
            webSocketDelivery.sendWebSocketNotification(user.id(), NotificationType.TASK_ASSIGNEE_UPDATED.name(), notificationMapper.toDto(notification));
            emailDelivery.sendEmail(user.email(), "Task Assignees Updated", message);
        });
    }

}
