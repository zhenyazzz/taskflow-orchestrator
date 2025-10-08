package org.example.analyticsservice.dto;

import java.util.Map;

public record TaskAnalyticsDto(
    Long totalTasks,
    Long createdTasks,
    Long completedTasks,
    Long deletedTasks,
    Double averageCompletionTimeHours,
    Map<String, Long> tasksByPriority,
    Map<String, Long> tasksByStatus,
    Map<java.time.LocalDate, Long> dailyCreatedTasks,
    Map<java.time.LocalDate, Long> dailyCompletedTasks
) {}
