package org.example.events.task;

import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;

import java.time.Instant;
import java.util.Set;

public record TaskUpdatedEvent(
        String id,
        String title,
        TaskPriority priority,
        Set<String> assigneeIds,
        String creatorId,
        Department department,
        Instant createdAt,
        Instant dueDate,
        Instant timestamp
) {
}
