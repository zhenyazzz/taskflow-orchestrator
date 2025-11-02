package org.example.events.comment;

import java.time.Instant;

public record CommentCreatedEvent(
        String id,
        String content,
        String authorId,
        Instant createdAt,
        String taskId
) {
}
