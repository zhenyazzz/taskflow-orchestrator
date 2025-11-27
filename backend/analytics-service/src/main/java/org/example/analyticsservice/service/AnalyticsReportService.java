package org.example.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.dto.LoginAnalyticsDto;
import org.example.analyticsservice.dto.TaskAnalyticsDto;
import org.example.analyticsservice.dto.UserAnalyticsDto;
import org.example.analyticsservice.repository.LoginMetricsRepository;
import org.example.analyticsservice.repository.TaskMetricsRepository;
import org.example.analyticsservice.repository.UserMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(name = "taskMetricsRepository")
public class AnalyticsReportService {

    private final TaskMetricsRepository taskMetricsRepository;
    private final UserMetricsRepository userMetricsRepository;
    private final LoginMetricsRepository loginMetricsRepository;

    public TaskAnalyticsDto getTaskAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Получение аналитики задач с {} по {}", startDate, endDate);

        Instant startInstant = startDate.atZone(java.time.ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atZone(java.time.ZoneId.systemDefault()).toInstant();

        Long createdTasks = taskMetricsRepository.countByEventTypeAndTimeBetween("CREATED", startInstant, endInstant);
        Long completedTasks = taskMetricsRepository.countByEventTypeAndTimeBetween("COMPLETED", startInstant, endInstant);
        Long deletedTasks = taskMetricsRepository.countByEventTypeAndTimeBetween("DELETED", startInstant, endInstant);
        Long totalTasks = createdTasks + completedTasks + deletedTasks;

        Double averageCompletionTime = taskMetricsRepository.getAverageCompletionTimeInSeconds(startInstant, endInstant);
        Double averageCompletionTimeHours = averageCompletionTime != null ? averageCompletionTime / 3600.0 : 0.0;

        Map<String, Long> tasksByPriority = convertToMap(
                taskMetricsRepository.countTasksByPriorityAndTimeBetween(startInstant, endInstant)
        );

        Map<String, Long> tasksByStatus = convertToMap(
                taskMetricsRepository.countTasksByStatusAndTimeBetween(startInstant, endInstant)
        );

        Map<java.time.LocalDate, Long> dailyCreatedTasks = convertDateToMap(
                taskMetricsRepository.countDailyTasksByEventTypeAndTimeBetween("CREATED", startInstant, endInstant)
        );

        Map<java.time.LocalDate, Long> dailyCompletedTasks = convertDateToMap(
                taskMetricsRepository.countDailyTasksByEventTypeAndTimeBetween("COMPLETED", startInstant, endInstant)
        );

        return new TaskAnalyticsDto(
                totalTasks,
                createdTasks,
                completedTasks,
                deletedTasks,
                averageCompletionTimeHours,
                tasksByPriority,
                tasksByStatus,
                dailyCreatedTasks,
                dailyCompletedTasks
        );
    }

    public UserAnalyticsDto getUserAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Получение аналитики пользователей с {} по {}", startDate, endDate);

        Instant startInstant = startDate.atZone(java.time.ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atZone(java.time.ZoneId.systemDefault()).toInstant();

        Long registeredUsers = userMetricsRepository.countByEventTypeAndTimeBetween("REGISTERED", startInstant, endInstant);
        Long updatedUsers = userMetricsRepository.countByEventTypeAndTimeBetween("UPDATED", startInstant, endInstant);
        Long totalUsers = registeredUsers + updatedUsers;

        Map<String, Long> usersByDepartment = convertToMap(
                userMetricsRepository.countUsersByDepartmentAndTimeBetween(startInstant, endInstant)
        );

        Map<String, Long> usersByRole = convertToMap(
                userMetricsRepository.countUsersByRoleAndTimeBetween(startInstant, endInstant)
        );

        return new UserAnalyticsDto(
                totalUsers,
                registeredUsers,
                updatedUsers,
                usersByDepartment,
                usersByRole
        );
    }

    public LoginAnalyticsDto getLoginAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Получение аналитики логинов с {} по {}", startDate, endDate);

        Instant startInstant = startDate.atZone(java.time.ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atZone(java.time.ZoneId.systemDefault()).toInstant();

        Long successfulLogins = loginMetricsRepository.countByLoginStatusAndTimeBetween("SUCCESS", startInstant, endInstant);
        Long failedLogins = loginMetricsRepository.countByLoginStatusAndTimeBetween("FAILED", startInstant, endInstant);
        Long totalLogins = successfulLogins + failedLogins;

        Double successRate = totalLogins > 0 ? (successfulLogins.doubleValue() / totalLogins.doubleValue()) * 100 : 0.0;

        Map<String, Long> failureReasons = convertToMap(
                loginMetricsRepository.countFailureReasonsByTimeBetween(startInstant, endInstant)
        );

        Map<LocalDate, Long> dailySuccessfulLogins = convertDateToMap(
                loginMetricsRepository.countDailyLoginsByStatusAndTimeBetween("SUCCESS", startInstant, endInstant)
        );

        Map<LocalDate, Long> dailyFailedLogins = convertDateToMap(
                loginMetricsRepository.countDailyLoginsByStatusAndTimeBetween("FAILED", startInstant, endInstant)
        );

        return new LoginAnalyticsDto(
                totalLogins,
                successfulLogins,
                failedLogins,
                successRate,
                failureReasons,
                dailySuccessfulLogins,
                dailyFailedLogins
        );
    }

    private Map<String, Long> convertToMap(java.util.List<Object[]> results) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] result : results) {
            String key = result[0] != null ? result[0].toString() : "Unknown";
            Long count = ((Number) result[1]).longValue();
            map.put(key, count);
        }
        return map;
    }

    private Map<LocalDate, Long> convertDateToMap(java.util.List<Object[]> results) {
        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            map.put(date, count);
        }
        return map;
    }
}
