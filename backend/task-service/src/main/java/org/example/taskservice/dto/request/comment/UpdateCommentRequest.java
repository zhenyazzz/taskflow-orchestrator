package org.example.taskservice.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentRequest(
        @NotBlank
        @NotNull
        String content
) {
}
