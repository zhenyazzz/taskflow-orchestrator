package org.example.taskservice.dto.response.task;

import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.example.taskservice.dto.response.comment.CommentResponse;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record TaskResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Set<String> assigneeIds,
        String creatorId,
        Department department,
        Instant createdAt,
        Instant dueDate,
        List<String> tags,
        List<CommentResponse> comments
) {
}
