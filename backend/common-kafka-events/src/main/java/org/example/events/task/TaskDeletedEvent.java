package org.example.events.task;

import org.example.events.enums.Department;

import java.time.Instant;
import java.util.Set;

public record TaskDeletedEvent(
        String id,
        String creatorId,
        String title,
        Set<String> assigneeIds,
        Department department,
        Instant timestamp
) {
}
