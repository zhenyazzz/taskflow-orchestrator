package org.example.analyticsservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.dto.*;
import org.example.analyticsservice.service.AnalyticsReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsReportService analyticsReportService;

    @GetMapping("/tasks")
    public ResponseEntity<TaskAnalyticsDto> getTaskAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        log.info("Получение аналитики задач с {} по {}", startDate, endDate);
        
        TaskAnalyticsDto analytics = analyticsReportService.getTaskAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/users")
    public ResponseEntity<UserAnalyticsDto> getUserAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        log.info("Получение аналитики пользователей с {} по {}", startDate, endDate);
        
        UserAnalyticsDto analytics = analyticsReportService.getUserAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/logins")
    public ResponseEntity<LoginAnalyticsDto> getLoginAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        log.info("Получение аналитики логинов с {} по {}", startDate, endDate);
        
        LoginAnalyticsDto analytics = analyticsReportService.getLoginAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        log.info("Получение данных дашборда с {} по {}", startDate, endDate);
        
        TaskAnalyticsDto taskAnalytics = analyticsReportService.getTaskAnalytics(startDate, endDate);
        UserAnalyticsDto userAnalytics = analyticsReportService.getUserAnalytics(startDate, endDate);
        LoginAnalyticsDto loginAnalytics = analyticsReportService.getLoginAnalytics(startDate, endDate);

        DashboardDto dashboard = new DashboardDto(
                taskAnalytics,
                userAnalytics,
                loginAnalytics,
                startDate,
                endDate
        );

        return ResponseEntity.ok(dashboard);
    }

    // DTO для дашборда
    public record DashboardDto(
        TaskAnalyticsDto taskAnalytics,
        UserAnalyticsDto userAnalytics,
        LoginAnalyticsDto loginAnalytics,
        LocalDateTime periodStart,
        LocalDateTime periodEnd
    ) {}
}
