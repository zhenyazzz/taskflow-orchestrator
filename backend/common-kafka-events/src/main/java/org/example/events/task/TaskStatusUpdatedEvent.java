package org.example.events.task;

import java.time.Instant;

import org.example.events.enums.Department;
import org.example.events.enums.TaskStatus;

public record TaskStatusUpdatedEvent(
    String id,
    String title,
    String userId,
    TaskStatus status,
    Department department,
    Instant updatedAt,
    Instant timestamp
) {

}
