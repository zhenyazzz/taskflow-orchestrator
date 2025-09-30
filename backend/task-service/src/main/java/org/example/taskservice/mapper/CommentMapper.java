package org.example.taskservice.mapper;


import org.example.events.comment.CommentCreatedEvent;
import org.example.events.comment.CommentDeletedEvent;
import org.example.events.comment.CommentUpdatedEvent;
import org.example.taskservice.dto.request.comment.CreateCommentRequest;
import org.example.taskservice.dto.request.comment.UpdateCommentRequest;
import org.example.taskservice.dto.response.comment.CommentResponse;
import org.example.taskservice.model.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CommentMapper {


    @Mappings({
            @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())"),
            @Mapping(target = "authorId", source = "authorId"),
            @Mapping(target = "content", source = "createCommentRequest.content"),
            @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    })
    Comment toComment(CreateCommentRequest createCommentRequest, String authorId);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "authorId", ignore = true),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    })
    void updateComment(UpdateCommentRequest updateCommentRequest, @MappingTarget Comment comment);

    CommentResponse toCommentResponse(Comment comment);

    CommentCreatedEvent toCommentCreatedEvent(Comment comment);

    @Mappings({
            @Mapping(target = "deletedAt", expression = "java(java.time.Instant.now())")
    })
    CommentDeletedEvent toCommentDeletedEvent(Comment comment);

    CommentUpdatedEvent toCommentUpdatedEvent(Comment comment);
}
