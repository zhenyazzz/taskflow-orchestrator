package org.example.taskservice.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.example.events.enums.TaskStatus;

@Schema(description = "Запрос на обновление статуса задачи")
public record UpdateStatusRequest(

        @NotNull(message = "Статус не может быть null")
        @Schema(
                description = "Новый статус задачи",
                implementation = TaskStatus.class,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TaskStatus status,

        @Schema(
                description = "Комментарий к изменению статуса",
                example = "Задача выполнена успешно",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        String comment

) {}
