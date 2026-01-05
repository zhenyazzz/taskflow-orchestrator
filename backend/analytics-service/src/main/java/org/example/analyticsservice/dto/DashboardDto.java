package org.example.analyticsservice.dto;

import java.util.List;

public record DashboardDto(
        TaskSummaryDto taskSummary,
        LoginAnalyticsDto loginAnalytics,
        List<UserTaskSummaryDto> topUsers
) {}
