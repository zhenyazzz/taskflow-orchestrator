package org.example.notificationservice.mapper;

import org.example.events.task.*;
import org.example.notificationservice.dto.response.UserResponse;
import org.example.notificationservice.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface TaskNotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_CREATED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskCreatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskCreatedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_UPDATED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskUpdatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskUpdatedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_SUBSCRIBED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskSubscribedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskSubscribedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_UNSUBSCRIBED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskUnsubscribedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskUnsubscribedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_COMPLETED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskCompletedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskCompletedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_DELETED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskDeletedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskDeletedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_STATUS_UPDATED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskStatusUpdatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskStatusUpdatedEvent event, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", constant = "TASK_ASSIGNEE_UPDATED")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskAssigneesUpdatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserResponse user, TaskAssigneesUpdatedEvent event, String message);

    @Named("mapTaskCreatedEventMetadata")
    default Map<String, String> mapTaskCreatedEventMetadata(TaskCreatedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "creatorId", event.creatorId()
        );
    }

    @Named("mapTaskUpdatedEventMetadata")
    default Map<String, String> mapTaskUpdatedEventMetadata(TaskUpdatedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "creatorId", event.creatorId()
        );
    }

    @Named("mapTaskSubscribedEventMetadata")
    default Map<String, String> mapTaskSubscribedEventMetadata(TaskSubscribedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "userId", event.userId()
        );
    }

    @Named("mapTaskUnsubscribedEventMetadata")
    default Map<String, String> mapTaskUnsubscribedEventMetadata(TaskUnsubscribedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "userId", event.userId()
        );
    }

    @Named("mapTaskCompletedEventMetadata")
    default Map<String, String> mapTaskCompletedEventMetadata(TaskCompletedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "creatorId", event.creatorId()
        );
    }

    @Named("mapTaskDeletedEventMetadata")
    default Map<String, String> mapTaskDeletedEventMetadata(TaskDeletedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "creatorId", event.creatorId()
        );
    }

    @Named("mapTaskStatusUpdatedEventMetadata")
    default Map<String, String> mapTaskStatusUpdatedEventMetadata(TaskStatusUpdatedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "status", event.status().name()
        );
    }

    @Named("mapTaskAssigneesUpdatedEventMetadata")
    default Map<String, String> mapTaskAssigneesUpdatedEventMetadata(TaskAssigneesUpdatedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "assigneeIds", String.join(",", event.assigneeIds())
        );
    }
}
