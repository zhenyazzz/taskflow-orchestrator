package org.example.analyticsservice.dto;

import java.time.LocalDate;
import java.util.Map;

public record LoginAnalyticsDto(
    Long totalLogins,
    Long successfulLogins,
    Long failedLogins,
    Double successRate,
    Map<String, Long> failureReasons,
    Map<LocalDate, Long> dailySuccessfulLogins,
    Map<LocalDate, Long> dailyFailedLogins
) {}
