package org.example.taskservice.dto.response.comment;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CommentResponse(
        String id,
        String content,
        String authorId,
        Instant createdAt,
        Instant updatedAt
) {
}
