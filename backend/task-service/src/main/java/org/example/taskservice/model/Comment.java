package org.example.taskservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class Comment {
    private String id;

    @NotNull
    private String content;

    @NotNull
    private String authorId;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;
}
