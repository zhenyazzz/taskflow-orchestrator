package org.example.analyticsservice.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO с персональной статистикой пользователя по задачам.
 */
public record UserTaskSummaryDto(
        String userId,
        LocalDate startDate,
        LocalDate endDate,
        Long totalTasks,
        Long completedTasks,
        Long inProgressTasks,
        Long pendingTasks,
        Long deletedTasks,
        Double completionPercentage,
        Map<String, Long> tasksByCategory,
        Map<String, Long> tasksByPriority,
        Map<String, Long> tasksByStatus,
        Map<String, Long> tasksByDepartment
) {}


