package org.example.analyticsservice.dto;

import java.time.LocalDate;
import java.util.Map;

public record TaskSummaryDto(
        LocalDate startDate,
        LocalDate endDate,
        Long totalTasks,
        Long completedTasks,
        Long inProgressTasks,
        Long pendingTasks,
        Long deletedTasks,
        Double completionPercentage,
        Double averageCompletionTimeHours,
        Map<String, Long> tasksByStatus,
        Map<String, Long> tasksByPriority,
        Map<String, Long> tasksByCategory,
        Map<String, Long> tasksByDepartment,
        Map<LocalDate, Long> dailyCreatedTasks,
        Map<LocalDate, Long> dailyCompletedTasks
) {}
