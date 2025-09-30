package org.example.taskservice.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Schema(description = "Запрос на обновление задачи")
public record UpdateTaskRequest(

        @Size(min = 1, max = 255, message = "Заголовок должен быть от 1 до 255 символов")
        @Schema(
                description = "Новый заголовок задачи",
                example = "Обновленный заголовок задачи",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String title,

        @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
        @Schema(
                description = "Новое описание задачи",
                example = "Обновленное описание задачи с деталями",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String description,

        @Schema(
                description = "Новый статус задачи",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        TaskStatus status,

        @Schema(
                description = "Новый приоритет задачи",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        TaskPriority priority,

        @Schema(
                description = "Новый дедлайн задачи",
                example = "2024-12-31T23:59:59Z",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Instant dueDate,

        @Schema(
                description = "Новые теги задачи",
                example = "[\"urgent\", \"backend\"]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        List<String> tags,

        @Schema(
                description = "Новые исполнители задачи",
                example = "[\"user123\", \"user456\"]",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Set<String> assigneeIds,

        @Schema(
                description = "Новый отдел для задачи",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Department department

) {}
