package org.example.events.task;

import org.example.events.enums.Department;

import java.time.Instant;
import java.util.Set;

public record TaskCompletedEvent(
        String id,
        String title,
        Set<String> assigneeIds,
        Department department,
        Instant completedAt,
        String userId,
        Instant timestamp
) {
}
