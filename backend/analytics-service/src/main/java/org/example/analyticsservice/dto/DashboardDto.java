package org.example.analyticsservice.dto;

import java.util.List;

/**
 * DTO с данными для дашборда аналитики.
 */
public record DashboardDto(
        TaskSummaryDto taskSummary,
        LoginAnalyticsDto loginAnalytics,
        List<UserTaskSummaryDto> topUsers
) {}


