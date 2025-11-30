package org.example.analyticsservice.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

/**
 * Общая статистика по задачам
 * Процент выполненных, общие метрики и т.д.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "task_statistics")
public class TaskStatistics {

    @Id
    private String id;

    @Field("date")
    private LocalDate date;

    // Общие счетчики
    @Field("total_tasks")
    @Builder.Default
    private Long totalTasks = 0L;

    @Field("completed_tasks")
    @Builder.Default
    private Long completedTasks = 0L;

    @Field("in_progress_tasks")
    @Builder.Default
    private Long inProgressTasks = 0L;

    @Field("pending_tasks")
    @Builder.Default
    private Long pendingTasks = 0L;

    @Field("deleted_tasks")
    @Builder.Default
    private Long deletedTasks = 0L;

    // Процент выполненных задач
    @Field("completion_percentage")
    @Builder.Default
    private Double completionPercentage = 0.0;

    // Задачи созданные сегодня
    @Field("created_tasks_today")
    @Builder.Default
    private Long createdTasksToday = 0L;

    // Задачи завершенные сегодня
    @Field("completed_tasks_today")
    @Builder.Default
    private Long completedTasksToday = 0L;

    // Задачи удаленные сегодня
    @Field("deleted_tasks_today")
    @Builder.Default
    private Long deletedTasksToday = 0L;

    // Задачи обновленные сегодня
    @Field("updated_tasks_today")
    @Builder.Default
    private Long updatedTasksToday = 0L;

    // Распределение по приоритетам
    @Field("tasks_by_priority")
    private Map<String, Long> tasksByPriority;

    // Распределение по статусам
    @Field("tasks_by_status")
    private Map<String, Long> tasksByStatus;

    // Распределение по департаментам
    @Field("tasks_by_department")
    private Map<String, Long> tasksByDepartment;

    // Распределение по категориям (для пирога)
    @Field("tasks_by_category")
    private Map<String, Long> tasksByCategory;

    @Field("last_updated")
    @Builder.Default
    private Instant lastUpdated = Instant.now();
}

