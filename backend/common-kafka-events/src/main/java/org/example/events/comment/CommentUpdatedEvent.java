package org.example.events.comment;

import java.time.Instant;

public record CommentUpdatedEvent(
        String id,
        String content,
        String authorId,
        Instant createdAt,
        Instant updatedAt,
        String taskId
) {
}
