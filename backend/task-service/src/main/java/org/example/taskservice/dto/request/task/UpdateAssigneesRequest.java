package org.example.taskservice.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record UpdateAssigneesRequest(
        @Schema(
                description = "Новые исполнители задачи",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Set<String> assigneeIds
) {
}
