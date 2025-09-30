package org.example.taskservice.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record CreateCommentRequest(
    @NotBlank
    @NotNull
    String content
){
}
