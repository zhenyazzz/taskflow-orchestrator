package org.example.taskservice.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;

import java.time.Instant;
import java.util.Set;
import java.util.List;

@Schema(description = "Request for creating a new task")
public record CreateTaskRequest(


        @NotBlank(message = "Task title is required")
        @Size(min = 1, max = 200, message = "Task title must be between 1 and 200 characters")
        @Schema(description = "Task title", example = "Develop new API", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        @Schema(description = "Task description", example = "Develop REST API for task management", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,

        @Schema(description = "Task priority", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH"}, requiredMode = Schema.RequiredMode.REQUIRED)
        TaskPriority priority,

        @Schema(description = "Assigned user IDs", example = "[\"user1\", \"user2\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Set<String> assigneeIds,

        @Schema(description = "Due date for the task", example = "2024-01-15T10:00:00Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Instant dueDate,

        @Schema(description = "Task tags", example = "[\"urgent\", \"backend\"]", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<String> tags,

        @Schema(description = "Department", example = "IT", allowableValues = {"IT", "HR", "Sales"}, requiredMode = Schema.RequiredMode.REQUIRED)
        Department department
) {
}

