package org.example.analyticsservice.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Set;

/**
 * Документ для хранения детальной информации о задаче
 * Используется для анализа и статистики
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class TaskDocument {

    @Id
    private String id;

    @Field("task_id")
    private String taskId;

    @Field("title")
    private String title;

    @Field("description_category")
    private String descriptionCategory; // Категория для пирога (на основе title/priority/department)

    @Field("priority")
    private TaskPriority priority;

    @Field("status")
    private TaskStatus status;

    @Field("department")
    private Department department;

    @Field("assignee_ids")
    private Set<String> assigneeIds;

    @Field("creator_id")
    private String creatorId;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Field("completed_at")
    private Instant completedAt;

    @Field("due_date")
    private Instant dueDate;

    @Field("is_completed")
    private Boolean isCompleted;

    @Field("is_deleted")
    private Boolean isDeleted;

    @Field("last_updated")
    private Instant lastUpdated;
}

