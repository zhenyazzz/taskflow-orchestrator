package org.example.events.task;

import java.time.Instant;

import org.example.events.enums.Department;

public record TaskUnsubscribedEvent(
    String id,
    String title,
    Department department,
    String userId,
    Instant timestamp
) {

}
