package org.example.analyticsservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.model.mongo.*;
import org.example.analyticsservice.repository.mongodb.*;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component; 

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
@Component 
@RequiredArgsConstructor
@Profile({"dev", "docker"})
public class DataSeeder {

    private static final String ADMIN_ID = "11111111-1111-1111-1111-111111111111";
    private static final String USER_ID = "22222222-2222-2222-2222-222222222222";
    private static final String USER1_ID = "33333333-3333-3333-3333-333333333333";
    private static final String USER2_ID = "44444444-4444-4444-4444-444444444444";
    private static final String USER3_ID = "55555555-5555-5555-5555-555555555555";
    private static final String USER4_ID = "66666666-6666-6666-6666-666666666666";

    // Мапа userId -> username для генерации DailyActiveUser
    private static final Map<String, String> USER_ID_TO_USERNAME = Map.of(
            ADMIN_ID, "admin",
            USER_ID, "user",
            USER1_ID, "user1",
            USER2_ID, "user2",
            USER3_ID, "user3",
            USER4_ID, "user4"
    );

    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatisticsRepository taskStatisticsRepository;
    private final TaskCounterRepository taskCounterRepository;
    private final UserTaskStatisticsRepository userTaskStatisticsRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final UserCounterRepository userCounterRepository;
    private final DailyActiveUserRepository dailyActiveUserRepository;

    public void seedIfEmpty() {
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

        taskCounterRepository.save(TaskCounter.builder()
                .id("global")
                .totalTasks(totalTasks)
                .lastUpdated(now)
                .build());

        // Генерируем TaskStatistics за последние 30 дней на основе фактических дат создания/обновления задач
        List<TaskStatistics> taskStatsList = buildTaskStatisticsForLast30Days(
                seeds, today, now, totalTasks, completedTasks, inProgressTasks, 
                pendingTasks, byStatus, byPriority, byCategory, byDepartment
        );
        taskStatisticsRepository.saveAll(taskStatsList);

        // --- User statistics & counters ---
        long totalUsers = 6L;
        
        // Генерируем UserStatistics за последние 30 дней
        List<UserStatistics> userStatsList = buildUserStatisticsForLast30Days(today, now, totalUsers);
        userStatisticsRepository.saveAll(userStatsList);

        userCounterRepository.save(UserCounter.builder()
                .id("global")
                .totalUsers(totalUsers)
                .lastUpdated(now)
                .build());

        // Генерируем DailyActiveUser для каждого пользователя за последние 30 дней
        List<DailyActiveUser> dailyActiveUsers = buildDailyActiveUsersForLast30Days(today);
        dailyActiveUserRepository.saveAll(dailyActiveUsers);

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
        List<TaskSeed> seeds = new ArrayList<>();
        
        // === IT Департамент ===
        // Завершенные задачи (разные даты)
        seeds.add(new TaskSeed("task-1", "Миграция базы данных на Postgres 15", "migration", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.IT,
                Set.of(USER1_ID, ADMIN_ID), now.minus(30, ChronoUnit.DAYS), now.minus(25, ChronoUnit.DAYS), now.minus(28, ChronoUnit.DAYS), now.minus(25, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-2", "Настройка CI/CD pipeline", "devops", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.IT,
                Set.of(USER1_ID, USER2_ID), now.minus(25, ChronoUnit.DAYS), now.minus(20, ChronoUnit.DAYS), now.minus(22, ChronoUnit.DAYS), now.minus(20, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-3", "Рефакторинг auth-service", "refactoring", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.IT,
                Set.of(USER_ID), now.minus(20, ChronoUnit.DAYS), now.minus(15, ChronoUnit.DAYS), now.minus(18, ChronoUnit.DAYS), now.minus(15, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-4", "Внедрение мониторинга Prometheus", "monitoring", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.IT,
                Set.of(USER2_ID), now.minus(15, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS), now.minus(12, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-5", "Оптимизация запросов к БД", "optimization", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.IT,
                Set.of(USER1_ID, USER3_ID), now.minus(12, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-6", "Обновление зависимостей", "maintenance", TaskStatus.COMPLETED, TaskPriority.LOW, Department.IT,
                Set.of(USER_ID), now.minus(10, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-7", "Настройка Grafana дашбордов", "monitoring", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.IT,
                Set.of(USER2_ID, USER4_ID), now.minus(8, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS), now.minus(6, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-8", "Реализация API для экспорта данных", "backend", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.IT,
                Set.of(USER3_ID), now.minus(6, ChronoUnit.DAYS), now.minus(3, ChronoUnit.DAYS), now.minus(4, ChronoUnit.DAYS), now.minus(3, ChronoUnit.DAYS)));
        
        // В работе
        seeds.add(new TaskSeed("task-9", "Фича: комментарии в задачах", "frontend", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.IT,
                Set.of(USER3_ID, USER4_ID), now.minus(5, ChronoUnit.DAYS), now, now.plus(7, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-10", "Интеграция с внешним API", "integration", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.IT,
                Set.of(USER_ID, USER1_ID), now.minus(4, ChronoUnit.DAYS), now, now.plus(5, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-11", "Написание unit-тестов", "testing", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, Department.IT,
                Set.of(USER2_ID), now.minus(3, ChronoUnit.DAYS), now, now.plus(10, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-12", "Улучшение производительности Kafka", "kafka", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.IT,
                Set.of(ADMIN_ID, USER1_ID), now.minus(6, ChronoUnit.DAYS), now, now.plus(14, ChronoUnit.DAYS), null));
        
        // Заблокированные
        seeds.add(new TaskSeed("task-13", "Ретраи в Kafka", "kafka", TaskStatus.BLOCKED, TaskPriority.HIGH, Department.IT,
                Set.of(USER2_ID), now.minus(7, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.plus(5, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-14", "Миграция на Kubernetes", "devops", TaskStatus.BLOCKED, TaskPriority.HIGH, Department.IT,
                Set.of(ADMIN_ID, USER1_ID, USER2_ID), now.minus(20, ChronoUnit.DAYS), now.minus(15, ChronoUnit.DAYS), now.plus(30, ChronoUnit.DAYS), null));
        
        // Доступные
        seeds.add(new TaskSeed("task-15", "Бэкап настроек MinIO", "backup", TaskStatus.AVAILABLE, TaskPriority.MEDIUM, Department.IT,
                Set.of(USER1_ID), now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS), now.plus(14, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-16", "Документация API", "documentation", TaskStatus.AVAILABLE, TaskPriority.LOW, Department.IT,
                Set.of(), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.plus(20, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-17", "Настройка логирования", "devops", TaskStatus.AVAILABLE, TaskPriority.MEDIUM, Department.IT,
                Set.of(), now, now, now.plus(12, ChronoUnit.DAYS), null));
        
        // === HR Департамент ===
        seeds.add(new TaskSeed("task-18", "План обучения команды", "training", TaskStatus.COMPLETED, TaskPriority.LOW, Department.HR,
                Set.of(USER3_ID), now.minus(18, ChronoUnit.DAYS), now.minus(15, ChronoUnit.DAYS), now.minus(16, ChronoUnit.DAYS), now.minus(15, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-19", "Организация корпоративного мероприятия", "event", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.HR,
                Set.of(USER4_ID), now.minus(14, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS), now.minus(12, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-20", "Обновление должностных инструкций", "documentation", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, Department.HR,
                Set.of(USER3_ID), now.minus(5, ChronoUnit.DAYS), now, now.plus(15, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-21", "Проведение performance review", "review", TaskStatus.AVAILABLE, TaskPriority.HIGH, Department.HR,
                Set.of(), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.plus(30, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-22", "Рекрутинг новых разработчиков", "recruitment", TaskStatus.AVAILABLE, TaskPriority.HIGH, Department.HR,
                Set.of(USER4_ID), now, now, now.plus(45, ChronoUnit.DAYS), null));
        
        // === FINANCE Департамент ===
        seeds.add(new TaskSeed("task-23", "Отчёт по метрикам за Q1", "reporting", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.FINANCE,
                Set.of(USER4_ID), now.minus(22, ChronoUnit.DAYS), now.minus(20, ChronoUnit.DAYS), now.minus(21, ChronoUnit.DAYS), now.minus(20, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-24", "Аудит финансовых операций", "audit", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.FINANCE,
                Set.of(ADMIN_ID), now.minus(16, ChronoUnit.DAYS), now.minus(12, ChronoUnit.DAYS), now.minus(14, ChronoUnit.DAYS), now.minus(12, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-25", "Бюджетирование на следующий квартал", "budgeting", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.FINANCE,
                Set.of(USER4_ID), now.minus(4, ChronoUnit.DAYS), now, now.plus(20, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-26", "Анализ расходов на облако", "analysis", TaskStatus.AVAILABLE, TaskPriority.MEDIUM, Department.FINANCE,
                Set.of(), now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS), now.plus(25, ChronoUnit.DAYS), null));
        
        // === MARKETING Департамент ===
        seeds.add(new TaskSeed("task-27", "Обновление UI-гайда", "design", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.MARKETING,
                Set.of(USER3_ID), now.minus(11, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS), now.minus(9, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-28", "Создание маркетинговой кампании", "campaign", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.MARKETING,
                Set.of(USER3_ID, USER4_ID), now.minus(9, ChronoUnit.DAYS), now.minus(6, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS), now.minus(6, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-29", "Анализ конкурентов", "research", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, Department.MARKETING,
                Set.of(USER3_ID), now.minus(3, ChronoUnit.DAYS), now, now.plus(12, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-30", "Обновление сайта компании", "website", TaskStatus.AVAILABLE, TaskPriority.MEDIUM, Department.MARKETING,
                Set.of(), now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), now.plus(35, ChronoUnit.DAYS), null));
        
        // === SALES Департамент ===
        seeds.add(new TaskSeed("task-31", "Анализ продаж за месяц", "analysis", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.SALES,
                Set.of(USER2_ID), now.minus(7, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS), now.minus(6, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-32", "Обучение команды продаж", "training", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.SALES,
                Set.of(USER2_ID, USER_ID), now.minus(13, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS), now.minus(11, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-33", "Внедрение CRM системы", "crm", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.SALES,
                Set.of(USER_ID, USER2_ID), now.minus(6, ChronoUnit.DAYS), now, now.plus(40, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-34", "Подготовка к выставке", "event", TaskStatus.AVAILABLE, TaskPriority.MEDIUM, Department.SALES,
                Set.of(), now, now, now.plus(60, ChronoUnit.DAYS), null));
        
        // === CUSTOMER_SERVICE Департамент ===
        seeds.add(new TaskSeed("task-35", "Улучшение FAQ раздела", "documentation", TaskStatus.COMPLETED, TaskPriority.LOW, Department.CUSTOMER_SERVICE,
                Set.of(USER4_ID), now.minus(10, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS), now.minus(8, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-36", "Обучение поддержки новым функциям", "training", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.CUSTOMER_SERVICE,
                Set.of(USER4_ID), now.minus(8, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS), now.minus(6, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-37", "Настройка системы тикетов", "support", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.CUSTOMER_SERVICE,
                Set.of(USER4_ID), now.minus(4, ChronoUnit.DAYS), now, now.plus(18, ChronoUnit.DAYS), null));
        
        // === PRODUCTION Департамент ===
        seeds.add(new TaskSeed("task-38", "Оптимизация производственных процессов", "optimization", TaskStatus.COMPLETED, TaskPriority.HIGH, Department.PRODUCTION,
                Set.of(USER1_ID), now.minus(19, ChronoUnit.DAYS), now.minus(16, ChronoUnit.DAYS), now.minus(17, ChronoUnit.DAYS), now.minus(16, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-39", "Внедрение системы контроля качества", "quality", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.PRODUCTION,
                Set.of(USER1_ID, USER2_ID), now.minus(5, ChronoUnit.DAYS), now, now.plus(25, ChronoUnit.DAYS), null));
        
        // === LOGISTICS Департамент ===
        seeds.add(new TaskSeed("task-40", "Обновление системы доставки", "delivery", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.LOGISTICS,
                Set.of(USER2_ID), now.minus(17, ChronoUnit.DAYS), now.minus(14, ChronoUnit.DAYS), now.minus(15, ChronoUnit.DAYS), now.minus(14, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-41", "Оптимизация маршрутов доставки", "optimization", TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM, Department.LOGISTICS,
                Set.of(USER2_ID), now.minus(3, ChronoUnit.DAYS), now, now.plus(21, ChronoUnit.DAYS), null));
        
        // === RESEARCH_AND_DEVELOPMENT Департамент ===
        seeds.add(new TaskSeed("task-42", "Исследование новых технологий", "research", TaskStatus.COMPLETED, TaskPriority.MEDIUM, Department.RESEARCH_AND_DEVELOPMENT,
                Set.of(ADMIN_ID, USER3_ID), now.minus(21, ChronoUnit.DAYS), now.minus(18, ChronoUnit.DAYS), now.minus(19, ChronoUnit.DAYS), now.minus(18, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-43", "Прототипирование новой функции", "prototype", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.RESEARCH_AND_DEVELOPMENT,
                Set.of(USER3_ID, USER4_ID), now.minus(7, ChronoUnit.DAYS), now, now.plus(50, ChronoUnit.DAYS), null));
        seeds.add(new TaskSeed("task-44", "Патентование нового алгоритма", "patent", TaskStatus.AVAILABLE, TaskPriority.LOW, Department.RESEARCH_AND_DEVELOPMENT,
                Set.of(), now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS), now.plus(90, ChronoUnit.DAYS), null));
        
        // === OTHER Департамент ===
        seeds.add(new TaskSeed("task-45", "Общее собрание команды", "meeting", TaskStatus.COMPLETED, TaskPriority.LOW, Department.OTHER,
                Set.of(ADMIN_ID), now.minus(4, ChronoUnit.DAYS), now.minus(4, ChronoUnit.DAYS), now.minus(4, ChronoUnit.DAYS), now.minus(4, ChronoUnit.DAYS)));
        seeds.add(new TaskSeed("task-46", "Обновление политики безопасности", "security", TaskStatus.IN_PROGRESS, TaskPriority.HIGH, Department.OTHER,
                Set.of(ADMIN_ID), now.minus(2, ChronoUnit.DAYS), now, now.plus(28, ChronoUnit.DAYS), null));
        
        return seeds;
    }

    private LocalDate toLocalDate(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Генерирует TaskStatistics за последние 30 дней на основе фактических дат задач
     */
    private List<TaskStatistics> buildTaskStatisticsForLast30Days(
            List<TaskSeed> seeds, LocalDate today, Instant now,
            long totalTasks, long completedTasks, long inProgressTasks, long pendingTasks,
            Map<String, Long> byStatus, Map<String, Long> byPriority,
            Map<String, Long> byCategory, Map<String, Long> byDepartment) {
        
        List<TaskStatistics> statsList = new ArrayList<>();
        
        for (int daysAgo = 30; daysAgo >= 0; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            
            // Подсчитываем задачи, созданные до этой даты (включительно)
            long tasksCreatedByDate = seeds.stream()
                    .filter(s -> !toLocalDate(s.createdAt()).isAfter(date))
                    .count();
            
            long tasksCompletedByDate = seeds.stream()
                    .filter(s -> s.completedAt() != null && !toLocalDate(s.completedAt()).isAfter(date))
                    .count();
            
            long tasksInProgressByDate = seeds.stream()
                    .filter(s -> s.status() == TaskStatus.IN_PROGRESS && !toLocalDate(s.createdAt()).isAfter(date) 
                            && (s.completedAt() == null || toLocalDate(s.completedAt()).isAfter(date)))
                    .count();
            
            long tasksPendingByDate = seeds.stream()
                    .filter(s -> s.status() == TaskStatus.AVAILABLE && !toLocalDate(s.createdAt()).isAfter(date))
                    .count();
            
            // Подсчитываем задачи, созданные/завершенные/обновленные в этот день
            long createdToday = seeds.stream()
                    .filter(s -> toLocalDate(s.createdAt()).isEqual(date))
                    .count();
            
            long completedToday = seeds.stream()
                    .filter(s -> s.completedAt() != null && toLocalDate(s.completedAt()).isEqual(date))
                    .count();
            
            long updatedToday = seeds.stream()
                    .filter(s -> s.updatedAt() != null && toLocalDate(s.updatedAt()).isEqual(date))
                    .count();
            
            // Строим распределения на основе задач, созданных до этой даты
            Map<String, Long> statusByDate = seeds.stream()
                    .filter(s -> !toLocalDate(s.createdAt()).isAfter(date))
                    .collect(Collectors.groupingBy(
                            s -> s.status().name(),
                            Collectors.counting()
                    ));
            
            Map<String, Long> priorityByDate = seeds.stream()
                    .filter(s -> !toLocalDate(s.createdAt()).isAfter(date))
                    .collect(Collectors.groupingBy(
                            s -> s.priority().name(),
                            Collectors.counting()
                    ));
            
            Map<String, Long> categoryByDate = seeds.stream()
                    .filter(s -> !toLocalDate(s.createdAt()).isAfter(date))
                    .collect(Collectors.groupingBy(
                            TaskSeed::category,
                            Collectors.counting()
                    ));
            
            Map<String, Long> departmentByDate = seeds.stream()
                    .filter(s -> !toLocalDate(s.createdAt()).isAfter(date))
                    .collect(Collectors.groupingBy(
                            s -> s.department().name(),
                            Collectors.counting()
                    ));
            
            double completionPercentage = tasksCreatedByDate == 0 ? 0.0 
                    : (tasksCompletedByDate * 100.0) / tasksCreatedByDate;
            
            TaskStatistics stats = TaskStatistics.builder()
                    .date(date)
                    .totalTasks(tasksCreatedByDate)
                    .completedTasks(tasksCompletedByDate)
                    .inProgressTasks(tasksInProgressByDate)
                    .pendingTasks(tasksPendingByDate)
                    .deletedTasks(0L)
                    .completionPercentage(completionPercentage)
                    .createdTasksToday(createdToday)
                    .completedTasksToday(completedToday)
                    .deletedTasksToday(0L)
                    .updatedTasksToday(updatedToday)
                    .tasksByStatus(statusByDate)
                    .tasksByPriority(priorityByDate)
                    .tasksByCategory(categoryByDate)
                    .tasksByDepartment(departmentByDate)
                    .lastUpdated(now.minus(daysAgo, ChronoUnit.DAYS))
                    .build();
            
            statsList.add(stats);
        }
        
        return statsList;
    }

    /**
     * Генерирует UserStatistics за последние 30 дней с реалистичными данными логинов
     */
    private List<UserStatistics> buildUserStatisticsForLast30Days(LocalDate today, Instant now, long totalUsers) {
        List<UserStatistics> statsList = new ArrayList<>();
        Random random = new Random(42); // Фиксированный seed для воспроизводимости
        
        for (int daysAgo = 30; daysAgo >= 0; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            
            // Генерируем реалистичные данные логинов
            // Больше логинов в будние дни, меньше в выходные
            boolean isWeekend = date.getDayOfWeek().getValue() >= 6;
            int baseSuccessfulLogins = isWeekend ? random.nextInt(5, 15) : random.nextInt(20, 50);
            int baseFailedLogins = random.nextInt(0, baseSuccessfulLogins / 10 + 1);
            
            // Больше активности в недавние дни
            double recencyFactor = 1.0 + (30.0 - daysAgo) / 30.0 * 0.5;
            long successfulLogins = Math.round(baseSuccessfulLogins * recencyFactor);
            long failedLogins = Math.round(baseFailedLogins * recencyFactor);
            
            // Активных пользователей в день (обычно меньше общего количества пользователей)
            long activeUsersToday = Math.min(totalUsers, Math.max(2, Math.round(successfulLogins / 2.5)));
            
            // Новые пользователи только в первые несколько дней
            long newUsersToday = daysAgo >= 28 ? random.nextLong(0, 2) : 0L;
            
            UserStatistics stats = UserStatistics.builder()
                    .date(date)
                    .totalUsers(totalUsers)
                    .newUsersToday(newUsersToday)
                    .activeUsersToday(activeUsersToday)
                    .successfulLogins(successfulLogins)
                    .failedLogins(failedLogins)
                    .lastUpdated(now.minus(daysAgo, ChronoUnit.DAYS))
                    .build();
            
            statsList.add(stats);
        }
        
        return statsList;
    }

    /**
     * Генерирует DailyActiveUser для каждого пользователя за последние 30 дней
     */
    private List<DailyActiveUser> buildDailyActiveUsersForLast30Days(LocalDate today) {
        List<DailyActiveUser> dailyActiveUsers = new ArrayList<>();
        Random random = new Random(42); // Фиксированный seed для воспроизводимости
        
        for (Map.Entry<String, String> entry : USER_ID_TO_USERNAME.entrySet()) {
            String username = entry.getValue();
            
            // Админ более активен, чем обычные пользователи
            int baseLoginCount = "admin".equals(username) ? 3 : 1;
            
            for (int daysAgo = 30; daysAgo >= 0; daysAgo--) {
                LocalDate date = today.minusDays(daysAgo);
                
                // Не все пользователи логинятся каждый день
                // Админ логинится чаще (80% дней), обычные пользователи реже (50-70% дней)
                boolean isWeekend = date.getDayOfWeek().getValue() >= 6;
                double loginProbability = "admin".equals(username) ? 0.8 : (isWeekend ? 0.4 : 0.6);
                
                if (random.nextDouble() < loginProbability) {
                    // Генерируем количество логинов в день (1-5 для обычных, 2-6 для админа)
                    long loginCount = baseLoginCount + random.nextLong(0, 4);
                    
                    // Больше активности в недавние дни
                    double recencyFactor = 1.0 + (30.0 - daysAgo) / 30.0 * 0.3;
                    loginCount = Math.round(loginCount * recencyFactor);
                    
                    DailyActiveUser dailyUser = DailyActiveUser.builder()
                            .username(username)
                            .date(date)
                            .loginCount(loginCount)
                            .build();
                    
                    dailyActiveUsers.add(dailyUser);
                }
            }
        }
        
        return dailyActiveUsers;
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

