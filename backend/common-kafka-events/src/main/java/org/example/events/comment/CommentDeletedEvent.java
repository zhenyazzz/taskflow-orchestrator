package org.example.events.comment;

import java.time.Instant;

public record CommentDeletedEvent(
        String id,
        String content,
        String authorId,
        Instant createdAt,
        Instant deletedAt,
        String taskId
) {
}
