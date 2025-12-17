package org.example.analyticsservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.dto.DashboardDto;
import org.example.analyticsservice.dto.LoginAnalyticsDto;
import org.example.analyticsservice.dto.TaskSummaryDto;
import org.example.analyticsservice.dto.UserTaskSummaryDto;
import org.example.analyticsservice.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/analytics")
@Slf4j
@RequiredArgsConstructor
public class AnalyticsController {

    private static final long DEFAULT_RANGE_DAYS = 30;

    private final AnalyticsService analyticsService;

    @GetMapping("/tasks")
    public ResponseEntity<TaskSummaryDto> getTaskAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        DateRange range = resolveRange(startDate, endDate);
        log.info("Запрос задач: {} — {}", range.start(), range.end());
        TaskSummaryDto summary = analyticsService.getTaskSummary(range.start(), range.end());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUserAnalytics(
            @RequestParam String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            DateRange range = resolveRange(startDate, endDate);
            log.info("Запрос аналитики пользователя {}: {} — {}", userId, range.start(), range.end());
            UserTaskSummaryDto summary = analyticsService.getUserTaskSummary(userId, range.start(), range.end());
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException ex) {
            log.warn("User analytics request rejected: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/logins")
    public ResponseEntity<LoginAnalyticsDto> getLoginAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        DateRange range = resolveRange(startDate, endDate);
        log.info("Запрос логинов: {} — {}", range.start(), range.end());
        LoginAnalyticsDto analytics = analyticsService.getLoginAnalytics(range.start(), range.end());
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        DateRange range = resolveRange(startDate, endDate);
        log.info("Запрос дашборда: {} — {}", range.start(), range.end());
        DashboardDto dashboard = analyticsService.getDashboard(range.start(), range.end());
        return ResponseEntity.ok(dashboard);
    }

    private DateRange resolveRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate resolvedEnd = Optional.ofNullable(endDateTime)
                .map(LocalDateTime::toLocalDate)
                .orElse(LocalDate.now());

        LocalDate resolvedStart = Optional.ofNullable(startDateTime)
                .map(LocalDateTime::toLocalDate)
                .orElse(resolvedEnd.minusDays(DEFAULT_RANGE_DAYS));

        if (resolvedStart.isAfter(resolvedEnd)) {
            LocalDate tmp = resolvedStart;
            resolvedStart = resolvedEnd;
            resolvedEnd = tmp;
        }

        return new DateRange(resolvedStart, resolvedEnd);
    }

    private record DateRange(LocalDate start, LocalDate end) {}
}
