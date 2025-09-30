package org.example.notificationservice.mapper;

import org.example.events.comment.CommentCreatedEvent;
import org.example.events.comment.CommentDeletedEvent;
import org.example.events.comment.CommentUpdatedEvent;
import org.example.notificationservice.dto.response.UserDto;
import org.example.notificationservice.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface CommentNotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "metadata", expression = "java(mapCommentCreatedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, CommentCreatedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "metadata", expression = "java(mapCommentUpdatedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, CommentUpdatedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "metadata", expression = "java(mapCommentDeletedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, CommentDeletedEvent event, String type, String message);

    @Named("mapCommentCreatedEventMetadata")
    default Map<String, String> mapCommentCreatedEventMetadata(CommentCreatedEvent event) {
        return Map.of(
                "taskId", event.taskId(),
                "commentId", event.id(),
                "content", event.content()
        );
    }

    @Named("mapCommentUpdatedEventMetadata")
    default Map<String, String> mapCommentUpdatedEventMetadata(CommentUpdatedEvent event) {
        return Map.of(
                "taskId", event.taskId(),
                "commentId", event.id(),
                "content", event.content()
        );
    }

    @Named("mapCommentDeletedEventMetadata")
    default Map<String, String> mapCommentDeletedEventMetadata(CommentDeletedEvent event) {
        return Map.of(
                "taskId", event.taskId(),
                "commentId", event.id()
        );
    }
}
