package org.example.analyticsservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.model.mongo.*;
import org.example.analyticsservice.repository.mongodb.*;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Наполняет Mongo данными аналитики, если коллекции пусты.
 * Использует те же пользователи/исполнители и задачи, что и dev сидеры других сервисов.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"dev", "docker"})
public class DataSeeder {

    private static final String ADMIN_ID = "11111111-1111-1111-1111-111111111111";
    private static final String USER_ID = "22222222-2222-2222-2222-222222222222";
    private static final String USER1_ID = "33333333-3333-3333-3333-333333333333";
    private static final String USER2_ID = "44444444-4444-4444-4444-444444444444";
    private static final String USER3_ID = "55555555-5555-5555-5555-555555555555";
    private static final String USER4_ID = "66666666-6666-6666-6666-666666666666";

    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatisticsRepository taskStatisticsRepository;
    private final TaskCounterRepository taskCounterRepository;
    private final UserTaskStatisticsRepository userTaskStatisticsRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserCounterRepository userCounterRepository;
    private final DailyActiveUserRepository dailyActiveUserRepository;

    @Bean
    @DependsOn("initializeMongoCollections")
    public CommandLineRunner seedAnalyticsData() {
        return args -> seedIfEmpty();
    }

    void seedIfEmpty() {
        if (taskDocumentRepository.count() > 0) {
            log.info("Analytics-service: коллекция task_documents уже заполнена, сидер пропущен");
            return;
        }

        Instant now = Instant.now();
        LocalDate today = LocalDate.now();

        List<TaskSeed> seeds = buildSeeds(now);

        // --- Task documents ---
        List<TaskDocument> documents = seeds.stream()
                .map(seed -> toTaskDocument(seed, now))
                .toList();
        taskDocumentRepository.saveAll(documents);

        // --- Global counters & statistics ---
        long totalTasks = seeds.size();
        long completedTasks = seeds.stream().filter(s -> s.status() == TaskStatus.COMPLETED).count();
        long inProgressTasks = seeds.stream().filter(s -> s.status() == TaskStatus.IN_PROGRESS).count();
        long pendingTasks = seeds.stream().filter(s -> s.status() == TaskStatus.AVAILABLE).count();

        Map<String, Long> byStatus = aggregate(seeds, seed -> seed.status().name());
        Map<String, Long> byPriority = aggregate(seeds, seed -> seed.priority().name());
        Map<String, Long> byDepartment = aggregate(seeds, seed -> seed.department().name());
        Map<String, Long> byCategory = aggregate(seeds, TaskSeed::category);

        long createdToday = seeds.stream().filter(s -> toLocalDate(s.createdAt()).isEqual(today)).count();
        long completedToday = seeds.stream()
                .filter(s -> s.completedAt() != null && toLocalDate(s.completedAt()).isEqual(today))
                .count();
        long updatedToday = seeds.stream().filter(s -> s.updatedAt() != null && toLocalDate(s.updatedAt()).isEqual(today)).count();

        taskCounterRepository.save(TaskCounter.builder()
                .id("global")
                .totalTasks(totalTasks)
                .lastUpdated(now)
                .build());

        TaskStatistics taskStats = taskStatisticsRepository.findByDate(today)
                .orElse(TaskStatistics.builder().date(today).build());
        taskStats.setTotalTasks(totalTasks);
        taskStats.setCompletedTasks(completedTasks);
        taskStats.setInProgressTasks(inProgressTasks);
        taskStats.setPendingTasks(pendingTasks);
        taskStats.setDeletedTasks(0L);
        taskStats.setCompletionPercentage(totalTasks == 0 ? 0.0 : completedTasks * 100.0 / totalTasks);
        taskStats.setCreatedTasksToday(createdToday);
        taskStats.setCompletedTasksToday(completedToday);
        taskStats.setDeletedTasksToday(0L);
        taskStats.setUpdatedTasksToday(updatedToday);
        taskStats.setTasksByStatus(byStatus);
        taskStats.setTasksByPriority(byPriority);
        taskStats.setTasksByCategory(byCategory);
        taskStats.setTasksByDepartment(byDepartment);
        taskStats.setLastUpdated(now);
        taskStatisticsRepository.save(taskStats);

        // --- User statistics & counters ---
        long totalUsers = 6L;
        UserStatistics userStats = userStatisticsRepository.findByDate(today)
                .orElse(UserStatistics.builder().date(today).build());
        userStats.setTotalUsers(totalUsers);
        userStats.setNewUsersToday(0L);
        userStats.setActiveUsersToday(4L);
        userStats.setSuccessfulLogins(12L);
        userStats.setFailedLogins(2L);
        userStats.setLastUpdated(now);
        userStatisticsRepository.save(userStats);

        userCounterRepository.save(UserCounter.builder()
                .id("global")
                .totalUsers(totalUsers)
                .lastUpdated(now)
                .build());

        dailyActiveUserRepository.saveAll(List.of(
                DailyActiveUser.builder().username("admin").date(today).loginCount(3L).build(),
                DailyActiveUser.builder().username("user").date(today).loginCount(2L).build(),
                DailyActiveUser.builder().username("user1").date(today).loginCount(2L).build(),
                DailyActiveUser.builder().username("user2").date(today).loginCount(1L).build(),
                DailyActiveUser.builder().username("user3").date(today).loginCount(1L).build()
        ));

        // --- User task statistics (per assignee) ---
        Set<String> assignees = seeds.stream()
                .flatMap(seed -> seed.assigneeIds().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        // Включаем админа, так как он создатель и участник одной задачи
        assignees.add(ADMIN_ID);

        List<UserTaskStatistics> userTaskStats = assignees.stream()
                .map(userId -> toUserTaskStats(userId, seeds, today, now))
                .filter(Objects::nonNull)
                .toList();
        userTaskStatisticsRepository.saveAll(userTaskStats);

        log.info("Analytics-service: создано {} task_documents, обновлены счётчики и статистики", documents.size());
    }

    private TaskDocument toTaskDocument(TaskSeed seed, Instant now) {
        boolean isCompleted = seed.status() == TaskStatus.COMPLETED;
        return TaskDocument.builder()
                .taskId(seed.taskId())
                .title(seed.title())
                .descriptionCategory(seed.category())
                .priority(seed.priority())
                .status(seed.status())
                .department(seed.department())
                .assigneeIds(new HashSet<>(seed.assigneeIds()))
                .creatorId(ADMIN_ID)
                .createdAt(seed.createdAt())
                .updatedAt(seed.updatedAt())
                .completedAt(seed.completedAt())
                .dueDate(seed.dueDate())
                .isCompleted(isCompleted)
                .isDeleted(false)
                .lastUpdated(now)
                .build();
    }

    private UserTaskStatistics toUserTaskStats(String userId, List<TaskSeed> seeds, LocalDate today, Instant now) {
        List<TaskSeed> userSeeds = seeds.stream()
                .filter(seed -> seed.assigneeIds().contains(userId))
                .toList();
        if (userSeeds.isEmpty()) {
            return null;
        }

        long total = userSeeds.size();
        long completed = userSeeds.stream().filter(s -> s.status() == TaskStatus.COMPLETED).count();
        long inProgress = userSeeds.stream().filter(s -> s.status() == TaskStatus.IN_PROGRESS).count();
        long pending = userSeeds.stream().filter(s -> s.status() == TaskStatus.AVAILABLE).count();
        long deleted = 0L;

        Map<String, Long> byCategory = aggregate(userSeeds, TaskSeed::category);
        Map<String, Long> byPriority = aggregate(userSeeds, seed -> seed.priority().name());
        Map<String, Long> byStatus = aggregate(userSeeds, seed -> seed.status().name());
        Map<String, Long> byDepartment = aggregate(userSeeds, seed -> seed.department().name());

        return UserTaskStatistics.builder()
                .userId(userId)
                .date(today)
                .totalTasks(total)
                .completedTasks(completed)
                .inProgressTasks(inProgress)
                .pendingTasks(pending)
                .deletedTasks(deleted)
                .completionPercentage(total == 0 ? 0.0 : completed * 100.0 / total)
                .tasksByCategory(byCategory)
                .tasksByPriority(byPriority)
                .tasksByStatus(byStatus)
                .tasksByDepartment(byDepartment)
                .lastUpdated(now)
                .build();
    }

    private Map<String, Long> aggregate(List<TaskSeed> seeds, Function<TaskSeed, String> keyExtractor) {
        return seeds.stream()
                .collect(Collectors.toMap(
                        keyExtractor,
                        seed -> 1L,
                        Long::sum,
                        LinkedHashMap::new
                ));
    }

    private List<TaskSeed> buildSeeds(Instant now) {
        return List.of(
                new TaskSeed("task-1", "Настроить CI/CD", "devops", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.IT,
                        Set.of(USER1_ID, USER2_ID), now.minus(4, ChronoUnit.DAYS), now, now.plus(5, ChronoUnit.DAYS), null),
                new TaskSeed("task-2", "Обновить UI-гайд", "design", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.MARKETING,
                        Set.of(USER3_ID), now.minus(5, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.HOURS)),
                new TaskSeed("task-3", "Миграция на Postgres 15", "migration", TaskStatus.BLOCKED, TaskPriority.HIGH, Department.IT,
                        Set.of(ADMIN_ID, USER1_ID), now.minus(6, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), null, null),
                new TaskSeed("task-4", "Тесты для auth-service", "testing", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, Department.IT,
                        Set.of(USER_ID), now.minus(2, ChronoUnit.DAYS), now, now.plus(3, ChronoUnit.DAYS), null),
                new TaskSeed("task-5", "Мониторинг Prometheus", "monitoring", TaskStatus.AVAILABLE, TaskPriority.HIGH, Department.IT,
                        Set.of(), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.plus(10, ChronoUnit.DAYS), null),
                new TaskSeed("task-6", "Отчёт по метрикам", "finance", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.FINANCE,
                        Set.of(USER4_ID), now.minus(4, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS)),
                new TaskSeed("task-7", "Ретраи в Kafka", "kafka", TaskStatus.BLOCKED, TaskPriority.HIGH, Department.IT,
                        Set.of(USER2_ID), now.minus(3, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), null, null),
                new TaskSeed("task-8", "План обучения команды", "training", TaskStatus.AVAILABLE, TaskPriority.LOW, Department.HR,
                        Set.of(), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.plus(20, ChronoUnit.DAYS), null),
                new TaskSeed("task-9", "Фича: комментарии в задачах", "comments", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.IT,
                        Set.of(USER3_ID, USER4_ID), now.minus(3, ChronoUnit.DAYS), now, now.plus(7, ChronoUnit.DAYS), null),
                new TaskSeed("task-10", "Бэкап настроек MinIO", "backup", TaskStatus.AVAILABLE, TaskPriority.MEDIUM, Department.IT,
                        Set.of(USER1_ID), now, now, now.plus(14, ChronoUnit.DAYS), null)
        );
    }

    private LocalDate toLocalDate(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private record TaskSeed(
            String taskId,
            String title,
            String category,
            TaskStatus status,
            TaskPriority priority,
            Department department,
            Set<String> assigneeIds,
            Instant createdAt,
            Instant updatedAt,
            Instant dueDate,
            Instant completedAt
    ) {
    }
}

