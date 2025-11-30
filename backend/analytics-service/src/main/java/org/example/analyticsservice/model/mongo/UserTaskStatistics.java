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
 * Статистика задач по пользователю
 * Включает процент выполнения, структурный анализ задач
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_task_statistics")
public class UserTaskStatistics {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("date")
    private LocalDate date;

    // Общая статистика
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

    // Процент выполнения
    @Field("completion_percentage")
    @Builder.Default
    private Double completionPercentage = 0.0;

    // Структурный анализ - распределение задач по категориям описаний (для пирога)
    @Field("tasks_by_category")
    private Map<String, Long> tasksByCategory; // Категория -> количество задач

    // Распределение по приоритетам
    @Field("tasks_by_priority")
    private Map<String, Long> tasksByPriority; // Приоритет -> количество задач

    // Распределение по статусам
    @Field("tasks_by_status")
    private Map<String, Long> tasksByStatus; // Статус -> количество задач

    // Распределение по департаментам
    @Field("tasks_by_department")
    private Map<String, Long> tasksByDepartment; // Департамент -> количество задач

    @Field("last_updated")
    @Builder.Default
    private Instant lastUpdated = Instant.now();
}

