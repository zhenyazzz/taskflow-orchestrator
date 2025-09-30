package org.example.analyticsservice.dto;

import java.util.Map;

public record UserAnalyticsDto(
    Long totalUsers,
    Long registeredUsers,
    Long updatedUsers,
    Map<String, Long> usersByDepartment,
    Map<String, Long> usersByRole
) {}
