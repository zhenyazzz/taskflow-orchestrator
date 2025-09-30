package org.example.taskservice.mapper;

import org.example.events.task.TaskAssigneesUpdatedEvent;
import org.example.events.task.TaskCompletedEvent;
import org.example.events.task.TaskCreatedEvent;
import org.example.events.task.TaskDeletedEvent;
import org.example.events.task.TaskStatusUpdatedEvent;
import org.example.events.task.TaskSubscribedEvent;
import org.example.events.task.TaskUnsubscribedEvent;
import org.example.events.task.TaskUpdatedEvent;
import org.example.taskservice.dto.request.task.CreateTaskRequest;
import org.example.taskservice.dto.request.task.UpdateAssigneesRequest;
import org.example.taskservice.dto.request.task.UpdateStatusRequest;
import org.example.taskservice.dto.request.task.UpdateTaskRequest;
import org.example.taskservice.dto.response.task.TaskResponse;
import org.example.taskservice.model.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    @Mappings({
            @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())"),
            @Mapping(target = "creatorId", source = "creatorId"),
            @Mapping(target = "status", expression = "java(TaskStatus.AVAILABLE)")
    })
    Task toTask(CreateTaskRequest createTaskRequest, String creatorId);

    @Mapping(target = "comments", expression = "java(task.getComments().stream().map(commentMapper::toCommentResponse).toList())")
    TaskResponse toResponse(Task task, CommentMapper commentMapper);

    void updateTask(UpdateTaskRequest updateTaskRequest, @MappingTarget Task task);

    void updateStatus(UpdateStatusRequest updateStatusRequest, @MappingTarget Task task);

    void updateAssignees(UpdateAssigneesRequest updateAssigneesRequest, @MappingTarget Task task);

    TaskCreatedEvent toTaskCreatedEvent(Task task);

    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    TaskUpdatedEvent toTaskUpdatedEvent(Task task);

    @Mappings({
            @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "userId", source = "userId")
    })
    TaskDeletedEvent toTaskDeletedEvent(Task task, String userId);

    @Mappings({
            @Mapping(target = "completedAt", source = "task.updatedAt"),
            @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "userId", source = "userId")
    })
    TaskCompletedEvent toTaskCompletedEvent(Task task, String userId);

    @Mappings({
            @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "userId", source = "userId")
    })
    TaskSubscribedEvent toTaskSubscribedEvent(Task updatedTask, String userId);

    @Mappings({
            @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())"),
            @Mapping(target = "userId", source = "userId")
    })
    TaskUnsubscribedEvent toTaskUnsubscribedEvent(Task updatedTask, String userId);

    @Mappings({
            @Mapping(target = "updatedAt", source = "updatedTask.updatedAt"),
            @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    })
    TaskStatusUpdatedEvent toTaskStatusUpdatedEvent(Task updatedTask);

    @Mappings({
            @Mapping(target = "updatedAt", source = "updatedTask.updatedAt"),
            @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    })
    TaskAssigneesUpdatedEvent toTaskAssigneesUpdatedEvent(Task updatedTask);
}
