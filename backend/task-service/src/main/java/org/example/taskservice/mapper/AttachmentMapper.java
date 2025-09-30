package org.example.taskservice.mapper;

import org.example.events.attachment.AttachmentAddedEvent;
import org.example.events.attachment.AttachmentDeletedEvent;
import org.example.taskservice.dto.response.attachment.AttachmentResponse;
import org.example.taskservice.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    
    AttachmentResponse toResponse(Attachment attachment);

    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    AttachmentAddedEvent toAttachmentAddedEvent(Attachment attachment, String uploadedBy);

    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    AttachmentDeletedEvent toAttachmentDeletedEvent(Attachment attachment, String deletedBy);

}
