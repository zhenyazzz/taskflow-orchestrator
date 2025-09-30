package org.example.notificationservice.mapper;

import org.example.events.attachment.AttachmentAddedEvent;
import org.example.events.attachment.AttachmentDeletedEvent;
import org.example.notificationservice.dto.response.UserDto;
import org.example.notificationservice.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface AttachmentNotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "metadata", expression = "java(mapAttachmentAddedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, AttachmentAddedEvent event, String type, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "metadata", expression = "java(mapAttachmentDeletedEventMetadata(event))")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toNotification(UserDto user, AttachmentDeletedEvent event, String type, String message);

    @Named("mapAttachmentAddedEventMetadata")
    default Map<String, String> mapAttachmentAddedEventMetadata(AttachmentAddedEvent event) {
        return Map.of(
                "taskId", event.taskId(),
                "attachmentId", event.id(),
                "fileName", event.fileName()
        );
    }

    @Named("mapAttachmentDeletedEventMetadata")
    default Map<String, String> mapAttachmentDeletedEventMetadata(AttachmentDeletedEvent event) {
        return Map.of(
                "taskId", event.taskId(),
                "attachmentId", event.id(),
                "fileName", event.fileName()
        );
    }
}
