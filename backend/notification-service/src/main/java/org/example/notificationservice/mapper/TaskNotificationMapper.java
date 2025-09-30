package org.example.notificationservice.mapper;

import org.example.events.task.*;
import org.example.notificationservice.dto.response.UserDto;
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
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskCreatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, TaskCreatedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskUpdatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, TaskUpdatedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskSubscribedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, TaskSubscribedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskUnsubscribedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, TaskUnsubscribedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskCompletedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, TaskCompletedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskDeletedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, TaskDeletedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskStatusUpdatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(String userId, TaskStatusUpdatedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "metadata", expression = "java(mapTaskAssigneesUpdatedEventMetadata(event))")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(String userId, TaskAssigneesUpdatedEvent event, String type, String message);

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
                "userId", event.userId()
        );
    }

    @Named("mapTaskDeletedEventMetadata")
    default Map<String, String> mapTaskDeletedEventMetadata(TaskDeletedEvent event) {
        return Map.of(
                "taskId", event.id(),
                "title", event.title(),
                "userId", event.userId()
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
