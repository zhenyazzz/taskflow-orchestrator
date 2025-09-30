package org.example.events.task;

import java.time.Instant;
import java.util.Set;

import org.example.events.enums.Department;

public record TaskAssigneesUpdatedEvent(
    String id,
    String title,
    Set<String> assigneeIds,
    Department department,
    Instant updatedAt,
    Instant timestamp
) {

}
