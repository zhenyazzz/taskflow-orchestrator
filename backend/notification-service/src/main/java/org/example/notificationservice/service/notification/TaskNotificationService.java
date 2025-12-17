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


    public void handleTaskCreated(TaskCreatedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.creatorId());
        String message = String.format("You created a new task '%s'.", event.title());
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());

        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );

        webSocketDelivery.sendWebSocketNotification(user.id(), "task-created", event);
        emailDelivery.sendEmail(user.email(), "New Task Created", message);
    }

    public void handleTaskUpdated(TaskUpdatedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.creatorId());
        String message = String.format("You updated task '%s'.", event.title());
        Set<String> recipientIds = new HashSet<>(event.assigneeIds());
        recipientIds.add(event.creatorId());

        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );
    }

    public void handleTaskSubscribed(TaskSubscribedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("%s subscribed to task '%s'.", user.firstName(), event.title());
        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );
        webSocketDelivery.sendWebSocketNotification(user.id(), "task-subscribed", event);
        emailDelivery.sendEmail(user.email(), "Task Subscription Notification", message);
    }

    public void handleTaskUnsubscribed(TaskUnsubscribedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("%s unsubscribed from task '%s'.", user.firstName(), event.title());
        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );
        webSocketDelivery.sendWebSocketNotification(user.id(), "task-unsubscribed", event);
    }

    // public void handleTaskCompleted(TaskCompletedEvent event) {
    //     UserResponse user = userServiceClient.getUserById(event.creatorId());
    //     String message = String.format("You completed task '%s'.", event.title());
    //     notificationRepository.save(
    //         taskNotificationMapper.toNotification(user, event, "TASK_COMPLETED", message)
    //     );
    //     webSocketDelivery.sendWebSocketNotification(user.id(), "task-completed", event);
    //     emailDelivery.sendEmail(user.email(), "Task Completed", message);
    // }

    // public void handleTaskDeleted(TaskDeletedEvent event) {
    //     UserResponse user = userServiceClient.getUserById(event.creatorId());
    //     String message = String.format("You deleted task '%s'.", event.title());
    //     notificationRepository.save(
    //         taskNotificationMapper.toNotification(user, event, "TASK_DELETED", message)
    //     );
    //     webSocketDelivery.sendWebSocketNotification(user.id(), "task-deleted", event);
    //     emailDelivery.sendEmail(user.email(), "Task Deleted", message);
    // }

    public void handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        UserResponse user = userServiceClient.getUserById(event.userId());
        String message = String.format("You updated task '%s' status to '%s'.", event.title(), event.status());
        notificationRepository.save(
            taskNotificationMapper.toNotification(user, event, message)
        );
        webSocketDelivery.sendWebSocketNotification(user.id(), "task-status-updated", event);
        emailDelivery.sendEmail(user.email(), "Task Status Updated", message);
    }

    // public void handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
    //     UserResponse user = userServiceClient.getUserById(event.userId());
    //     String message = String.format("You updated task '%s' assignees to '%s'.", event.title(), event.assigneeIds());
    //     notificationRepository.save(
    //         taskNotificationMapper.toNotification(user, event, "TASK_ASSIGNEE_UPDATED", message)
    //     );
    //     webSocketDelivery.sendWebSocketNotification(user.id(), "task-assignee-updated", event);
    //     emailDelivery.sendEmail(user.email(), "Task Assignees Updated", message);
    // }
    // }    

}
