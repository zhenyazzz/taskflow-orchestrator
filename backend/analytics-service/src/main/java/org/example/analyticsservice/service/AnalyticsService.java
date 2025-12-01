package org.example.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.dto.DashboardDto;
import org.example.analyticsservice.dto.LoginAnalyticsDto;
import org.example.analyticsservice.dto.TaskSummaryDto;
import org.example.analyticsservice.dto.UserTaskSummaryDto;
import org.example.analyticsservice.model.mongo.*;
import org.example.analyticsservice.repository.mongodb.*;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.example.events.task.*;
import org.example.events.user.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final UserStatisticsRepository userStatisticsRepository;
    private final DailyActiveUserRepository dailyActiveUserRepository;
    private final UserCounterRepository userCounterRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatisticsRepository taskStatisticsRepository;
    private final TaskCounterRepository taskCounterRepository;
    private final UserTaskStatisticsRepository userTaskStatisticsRepository;

    // ---------- Публичные методы для REST контроллеров ----------

    public TaskSummaryDto getTaskSummary(LocalDate startDate, LocalDate endDate) {
        List<TaskStatistics> statsInRange = taskStatisticsRepository.findByDateBetween(startDate, endDate);
        statsInRange.sort(Comparator.comparing(TaskStatistics::getDate));

        TaskStatistics latest = resolveLatestTaskStats(statsInRange, endDate);
        Map<LocalDate, Long> createdPerDay = buildDailyTaskMap(statsInRange, TaskStatistics::getCreatedTasksToday);
        Map<LocalDate, Long> completedPerDay = buildDailyTaskMap(statsInRange, TaskStatistics::getCompletedTasksToday);

        return new TaskSummaryDto(
                startDate,
                endDate,
                latest != null ? safeLong(latest.getTotalTasks()) : 0L,
                latest != null ? safeLong(latest.getCompletedTasks()) : 0L,
                latest != null ? safeLong(latest.getInProgressTasks()) : 0L,
                latest != null ? safeLong(latest.getPendingTasks()) : 0L,
                latest != null ? safeLong(latest.getDeletedTasks()) : 0L,
                latest != null ? safeDouble(latest.getCompletionPercentage()) : 0.0,
                calculateAverageCompletionTimeHours(startDate, endDate),
                latest != null ? copyOrEmpty(latest.getTasksByStatus()) : Collections.emptyMap(),
                latest != null ? copyOrEmpty(latest.getTasksByPriority()) : Collections.emptyMap(),
                latest != null ? copyOrEmpty(latest.getTasksByCategory()) : Collections.emptyMap(),
                latest != null ? copyOrEmpty(latest.getTasksByDepartment()) : Collections.emptyMap(),
                createdPerDay,
                completedPerDay
        );
    }

    public UserTaskSummaryDto getUserTaskSummary(String userId, LocalDate startDate, LocalDate endDate) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }

        List<UserTaskStatistics> statsInRange = userTaskStatisticsRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        statsInRange.sort(Comparator.comparing(UserTaskStatistics::getDate));

        UserTaskStatistics latest = resolveLatestUserStats(userId, statsInRange, endDate);

        Map<String, Long> categoryDistribution = aggregateDistributions(statsInRange, UserTaskStatistics::getTasksByCategory);
        Map<String, Long> priorityDistribution = aggregateDistributions(statsInRange, UserTaskStatistics::getTasksByPriority);
        Map<String, Long> statusDistribution = aggregateDistributions(statsInRange, UserTaskStatistics::getTasksByStatus);
        Map<String, Long> departmentDistribution = aggregateDistributions(statsInRange, UserTaskStatistics::getTasksByDepartment);

        if (categoryDistribution.isEmpty() && latest != null) {
            categoryDistribution = copyOrEmpty(latest.getTasksByCategory());
        }
        if (priorityDistribution.isEmpty() && latest != null) {
            priorityDistribution = copyOrEmpty(latest.getTasksByPriority());
        }
        if (statusDistribution.isEmpty() && latest != null) {
            statusDistribution = copyOrEmpty(latest.getTasksByStatus());
        }
        if (departmentDistribution.isEmpty() && latest != null) {
            departmentDistribution = copyOrEmpty(latest.getTasksByDepartment());
        }

        return new UserTaskSummaryDto(
                userId,
                startDate,
                endDate,
                latest != null ? safeLong(latest.getTotalTasks()) : 0L,
                latest != null ? safeLong(latest.getCompletedTasks()) : 0L,
                latest != null ? safeLong(latest.getInProgressTasks()) : 0L,
                latest != null ? safeLong(latest.getPendingTasks()) : 0L,
                latest != null ? safeLong(latest.getDeletedTasks()) : 0L,
                latest != null ? safeDouble(latest.getCompletionPercentage()) : 0.0,
                categoryDistribution,
                priorityDistribution,
                statusDistribution,
                departmentDistribution
        );
    }

    public LoginAnalyticsDto getLoginAnalytics(LocalDate startDate, LocalDate endDate) {
        List<UserStatistics> stats = userStatisticsRepository.findByDateBetween(startDate, endDate);
        stats.sort(Comparator.comparing(UserStatistics::getDate));

        long successful = stats.stream().mapToLong(s -> safeLong(s.getSuccessfulLogins())).sum();
        long failed = stats.stream().mapToLong(s -> safeLong(s.getFailedLogins())).sum();
        long total = successful + failed;
        double successRate = total > 0 ? (successful * 100.0) / total : 0.0;

        Map<LocalDate, Long> dailySuccessful = stats.stream()
                .collect(Collectors.toMap(
                        UserStatistics::getDate,
                        s -> safeLong(s.getSuccessfulLogins()),
                        (left, right) -> right,
                        LinkedHashMap::new
                ));

        Map<LocalDate, Long> dailyFailed = stats.stream()
                .collect(Collectors.toMap(
                        UserStatistics::getDate,
                        s -> safeLong(s.getFailedLogins()),
                        (left, right) -> right,
                        LinkedHashMap::new
                ));

        return new LoginAnalyticsDto(
                total,
                successful,
                failed,
                successRate,
                Collections.emptyMap(),
                dailySuccessful,
                dailyFailed
        );
    }

    public DashboardDto getDashboard(LocalDate startDate, LocalDate endDate) {
        TaskSummaryDto taskSummary = getTaskSummary(startDate, endDate);
        LoginAnalyticsDto loginAnalytics = getLoginAnalytics(startDate, endDate);

        List<UserTaskStatistics> stats = userTaskStatisticsRepository.findByDateBetween(startDate, endDate);
        Map<String, UserTaskStatistics> latestByUser = new HashMap<>();

        for (UserTaskStatistics statistic : stats) {
            latestByUser.merge(
                    statistic.getUserId(),
                    statistic,
                    (existing, incoming) -> existing.getDate().isAfter(incoming.getDate()) ? existing : incoming
            );
        }

        List<UserTaskSummaryDto> topUsers = latestByUser.values().stream()
                .map(stat -> new UserTaskSummaryDto(
                        stat.getUserId(),
                        startDate,
                        endDate,
                        safeLong(stat.getTotalTasks()),
                        safeLong(stat.getCompletedTasks()),
                        safeLong(stat.getInProgressTasks()),
                        safeLong(stat.getPendingTasks()),
                        safeLong(stat.getDeletedTasks()),
                        safeDouble(stat.getCompletionPercentage()),
                        copyOrEmpty(stat.getTasksByCategory()),
                        copyOrEmpty(stat.getTasksByPriority()),
                        copyOrEmpty(stat.getTasksByStatus()),
                        copyOrEmpty(stat.getTasksByDepartment())
                ))
                .sorted(Comparator.comparing(UserTaskSummaryDto::completionPercentage).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return new DashboardDto(taskSummary, loginAnalytics, topUsers);
    }

    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Processing task created event for task: {}", event.id());
        
        try {
            // Сохраняем или обновляем документ задачи
            TaskDocument taskDoc = taskDocumentRepository.findByTaskId(event.id())
                    .orElse(TaskDocument.builder()
                            .taskId(event.id())
                            .isCompleted(false)
                            .isDeleted(false)
                            .build());
            
            taskDoc.setTitle(event.title());
            taskDoc.setPriority(event.priority());
            taskDoc.setDepartment(event.department());
            taskDoc.setCreatorId(event.creatorId());
            taskDoc.setAssigneeIds(event.assigneeIds());
            taskDoc.setCreatedAt(event.createdAt());
            taskDoc.setDueDate(event.dueDate());
            taskDoc.setDescriptionCategory(extractCategory(event.title(), event.priority(), event.department()));
            taskDoc.setStatus(TaskStatus.AVAILABLE);
            taskDoc.setLastUpdated(Instant.now());
            
            taskDocumentRepository.save(taskDoc);
            
            // Обновляем общую статистику
            updateTaskStatisticsOnCreate(event);
            
            // Обновляем статистику по пользователям
            updateUserTaskStatisticsOnCreate(event);
            
            // Обновляем счетчик задач
            taskCounterRepository.ensureExists();
            taskCounterRepository.increment(1L, Instant.now());
            
            log.debug("Сохранена метрика создания задачи: {}", event.id());
        } catch (Exception e) {
            log.error("Error processing task created event: {}", e.getMessage(), e);
        }
    }

    public void handleTaskUpdated(TaskUpdatedEvent event) {
        log.info("Processing task updated event for task: {}", event.id());
        
        try {
            Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
            if (taskOpt.isPresent()) {
                TaskDocument taskDoc = taskOpt.get();
                taskDoc.setTitle(event.title());
                taskDoc.setPriority(event.priority());
                taskDoc.setDepartment(event.department());
                taskDoc.setAssigneeIds(event.assigneeIds());
                taskDoc.setUpdatedAt(event.timestamp());
                taskDoc.setDescriptionCategory(extractCategory(event.title(), event.priority(), event.department()));
                taskDoc.setLastUpdated(Instant.now());
                
                taskDocumentRepository.save(taskDoc);
                
                // Обновляем статистику
                updateTaskStatisticsOnUpdate();
                log.debug("Сохранена метрика обновления задачи: {}", event.id());
            }
        } catch (Exception e) {
            log.error("Error processing task updated event: {}", e.getMessage(), e);
        }
    }

    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Processing task completed event for task: {}", event.id());
        
        try {
            Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
            if (taskOpt.isPresent()) {
                TaskDocument taskDoc = taskOpt.get();
                taskDoc.setIsCompleted(true);
                taskDoc.setCompletedAt(event.completedAt());
                taskDoc.setStatus(TaskStatus.COMPLETED);
                taskDoc.setLastUpdated(Instant.now());
                
                taskDocumentRepository.save(taskDoc);
                
                // Обновляем статистику
                updateTaskStatisticsOnComplete(event);
                updateUserTaskStatisticsOnComplete(event);
                
                log.debug("Сохранена метрика завершения задачи: {}", event.id());
            }
        } catch (Exception e) {
            log.error("Error processing task completed event: {}", e.getMessage(), e);
        }
    }

    public void handleTaskDeleted(TaskDeletedEvent event) {
        log.info("Processing task deleted event for task: {}", event.id());
        
        try {
            Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
            if (taskOpt.isPresent()) {
                TaskDocument taskDoc = taskOpt.get();
                taskDoc.setIsDeleted(true);
                taskDoc.setLastUpdated(Instant.now());
                
                taskDocumentRepository.save(taskDoc);
                
                // Обновляем статистику
                updateTaskStatisticsOnDelete(event);
                updateUserTaskStatisticsOnDelete(event);
                
                log.debug("Сохранена метрика удаления задачи: {}", event.id());
            }
        } catch (Exception e) {
            log.error("Error processing task deleted event: {}", e.getMessage(), e);
        }
    }

    public void handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Processing task status updated event for task: {}", event.id());
        
        try {
            Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
            if (taskOpt.isPresent()) {
                TaskDocument taskDoc = taskOpt.get();
                taskDoc.setStatus(event.status());
                taskDoc.setUpdatedAt(event.updatedAt());
                taskDoc.setLastUpdated(Instant.now());
                
                if (event.status() == TaskStatus.COMPLETED) {
                    taskDoc.setIsCompleted(true);
                    taskDoc.setCompletedAt(event.updatedAt());
                }
                
                taskDocumentRepository.save(taskDoc);
                
                // Обновляем статистику по статусам
                updateTaskStatisticsOnStatusUpdate(event);
                updateUserTaskStatisticsOnStatusUpdate(event);
                
                log.debug("Сохранена метрика обновления статуса задачи: {}", event.id());
            }
        } catch (Exception e) {
            log.error("Error processing task status updated event: {}", e.getMessage(), e);
        }
    }

    public void handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Processing task assignees updated event for task: {}", event.id());
        
        try {
            Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
            if (taskOpt.isPresent()) {
                TaskDocument taskDoc = taskOpt.get();
                taskDoc.setAssigneeIds(event.assigneeIds());
                taskDoc.setUpdatedAt(event.updatedAt());
                taskDoc.setLastUpdated(Instant.now());
                
                taskDocumentRepository.save(taskDoc);
                log.debug("Сохранена метрика обновления исполнителей задачи: {}", event.id());
            }
        } catch (Exception e) {
            log.error("Error processing task assignees updated event: {}", e.getMessage(), e);
        }
    }

    // Методы для обновления общей статистики
    private void updateTaskStatisticsOnCreate(TaskCreatedEvent event) {
        LocalDate today = LocalDate.now();
        TaskStatistics stats = taskStatisticsRepository.findByDate(today)
                .orElse(TaskStatistics.builder()
                        .date(today)
                        .totalTasks(0L)
                        .completedTasks(0L)
                        .inProgressTasks(0L)
                        .pendingTasks(0L)
                        .deletedTasks(0L)
                        .createdTasksToday(0L)
                        .completedTasksToday(0L)
                        .deletedTasksToday(0L)
                        .updatedTasksToday(0L)
                        .completionPercentage(0.0)
                        .build());
        
        if (stats.getId() == null) {
            stats = taskStatisticsRepository.save(stats);
        }
        
        taskStatisticsRepository.incrementTotalTasks(today, 1L, Instant.now());
        taskStatisticsRepository.incrementCreatedTasksToday(today, 1L, Instant.now());
        taskStatisticsRepository.incrementPendingTasks(today, 1L, Instant.now());
        
        recalculateTaskStatistics(today);
    }

    private void updateTaskStatisticsOnUpdate() {
        LocalDate today = LocalDate.now();
        taskStatisticsRepository.incrementUpdatedTasksToday(today, 1L, Instant.now());
    }

    private void updateTaskStatisticsOnComplete(TaskCompletedEvent event) {
        LocalDate today = LocalDate.now();
        
        Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
        if (taskOpt.isPresent()) {
            TaskDocument task = taskOpt.get();
            TaskStatus oldStatus = task.getStatus();
            
            // Уменьшаем счетчик старого статуса
            if (oldStatus == TaskStatus.IN_PROGRESS) {
                taskStatisticsRepository.incrementInProgressTasks(today, -1L, Instant.now());
            } else if (oldStatus == TaskStatus.AVAILABLE) {
                taskStatisticsRepository.incrementPendingTasks(today, -1L, Instant.now());
            }
        }
        
        taskStatisticsRepository.incrementCompletedTasks(today, 1L, Instant.now());
        taskStatisticsRepository.incrementCompletedTasksToday(today, 1L, Instant.now());
        recalculateTaskStatistics(today);
    }

    private void updateTaskStatisticsOnDelete(TaskDeletedEvent event) {
        LocalDate today = LocalDate.now();
        taskStatisticsRepository.incrementDeletedTasks(today, 1L, Instant.now());
        taskStatisticsRepository.incrementDeletedTasksToday(today, 1L, Instant.now());
        recalculateTaskStatistics(today);
    }

    private void updateTaskStatisticsOnStatusUpdate(TaskStatusUpdatedEvent event) {
        LocalDate today = LocalDate.now();
        TaskStatus status = event.status();
        
        Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
        if (taskOpt.isPresent()) {
            TaskDocument task = taskOpt.get();
            TaskStatus oldStatus = task.getStatus();
            
            // Уменьшаем счетчик старого статуса
            if (oldStatus == TaskStatus.COMPLETED) {
                // Не уменьшаем completed, так как задача уже была завершена
            } else if (oldStatus == TaskStatus.IN_PROGRESS) {
                taskStatisticsRepository.incrementInProgressTasks(today, -1L, Instant.now());
            } else if (oldStatus == TaskStatus.AVAILABLE) {
                taskStatisticsRepository.incrementPendingTasks(today, -1L, Instant.now());
            }
            
            // Увеличиваем счетчик нового статуса
            if (status == TaskStatus.COMPLETED) {
                taskStatisticsRepository.incrementCompletedTasks(today, 1L, Instant.now());
                taskStatisticsRepository.incrementCompletedTasksToday(today, 1L, Instant.now());
            } else if (status == TaskStatus.IN_PROGRESS) {
                taskStatisticsRepository.incrementInProgressTasks(today, 1L, Instant.now());
            } else if (status == TaskStatus.AVAILABLE) {
                taskStatisticsRepository.incrementPendingTasks(today, 1L, Instant.now());
            }
        }
        
        recalculateTaskStatistics(today);
    }

    private void recalculateTaskStatistics(LocalDate date) {
        TaskStatistics stats = taskStatisticsRepository.findByDate(date).orElse(null);
        if (stats == null) return;
        
        // Пересчитываем проценты и распределения
        long total = stats.getTotalTasks();
        long completed = stats.getCompletedTasks();
        
        double completionPercentage = total > 0 ? (completed * 100.0 / total) : 0.0;
        taskStatisticsRepository.updateCompletionPercentage(date, completionPercentage, Instant.now());
        
        // Обновляем распределения по категориям, приоритетам, статусам
        updateTaskStatisticsDistributions(date);
    }

    private void updateTaskStatisticsDistributions(LocalDate date) {
        List<TaskDocument> allTasks = taskDocumentRepository.findAllActiveTasks();
        
        Map<String, Long> byCategory = allTasks.stream()
                .filter(t -> t.getDescriptionCategory() != null)
                .collect(Collectors.groupingBy(
                        TaskDocument::getDescriptionCategory,
                        Collectors.counting()));
        
        Map<String, Long> byPriority = allTasks.stream()
                .filter(t -> t.getPriority() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()));
        
        Map<String, Long> byStatus = allTasks.stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()));
        
        Map<String, Long> byDepartment = allTasks.stream()
                .filter(t -> t.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getDepartment().name(),
                        Collectors.counting()));
        
        TaskStatistics stats = taskStatisticsRepository.findByDate(date).orElse(null);
        if (stats != null) {
            stats.setTasksByCategory(byCategory);
            stats.setTasksByPriority(byPriority);
            stats.setTasksByStatus(byStatus);
            stats.setTasksByDepartment(byDepartment);
            stats.setLastUpdated(Instant.now());
            taskStatisticsRepository.save(stats);
        }
    }

    // Методы для обновления статистики по пользователям
    private void updateUserTaskStatisticsOnCreate(TaskCreatedEvent event) {
        LocalDate today = LocalDate.now();
        
        // Обновляем статистику для создателя
        if (event.creatorId() != null) {
            updateUserTaskStatistics(event.creatorId(), today, stats -> {
                stats.setTotalTasks(stats.getTotalTasks() + 1);
                stats.setPendingTasks(stats.getPendingTasks() + 1);
            });
        }
        
        // Обновляем статистику для исполнителей
        if (event.assigneeIds() != null) {
            for (String assigneeId : event.assigneeIds()) {
                updateUserTaskStatistics(assigneeId, today, stats -> {
                    stats.setTotalTasks(stats.getTotalTasks() + 1);
                    stats.setPendingTasks(stats.getPendingTasks() + 1);
                });
            }
        }
    }

    private void updateUserTaskStatisticsOnComplete(TaskCompletedEvent event) {
        LocalDate today = LocalDate.now();
        
        Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
        if (taskOpt.isPresent()) {
            TaskDocument task = taskOpt.get();
            TaskStatus oldStatus = task.getStatus();
            
            if (event.assigneeIds() != null) {
                for (String assigneeId : event.assigneeIds()) {
                    updateUserTaskStatistics(assigneeId, today, stats -> {
                        stats.setCompletedTasks(stats.getCompletedTasks() + 1);
                        
                        // Уменьшаем счетчик старого статуса
                        if (oldStatus == TaskStatus.IN_PROGRESS) {
                            stats.setInProgressTasks(Math.max(0, stats.getInProgressTasks() - 1));
                        } else if (oldStatus == TaskStatus.AVAILABLE) {
                            stats.setPendingTasks(Math.max(0, stats.getPendingTasks() - 1));
                        }
                    });
                }
            }
        }
    }

    private void updateUserTaskStatisticsOnDelete(TaskDeletedEvent event) {
        LocalDate today = LocalDate.now();
        
        Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
        if (taskOpt.isPresent()) {
            TaskDocument task = taskOpt.get();
            
            if (task.getCreatorId() != null) {
                updateUserTaskStatistics(task.getCreatorId(), today, stats -> {
                    stats.setDeletedTasks(stats.getDeletedTasks() + 1);
                    stats.setTotalTasks(Math.max(0, stats.getTotalTasks() - 1));
                });
            }
            
            if (task.getAssigneeIds() != null) {
                for (String assigneeId : task.getAssigneeIds()) {
                    updateUserTaskStatistics(assigneeId, today, stats -> {
                        stats.setDeletedTasks(stats.getDeletedTasks() + 1);
                        stats.setTotalTasks(Math.max(0, stats.getTotalTasks() - 1));
                    });
                }
            }
        }
    }

    private void updateUserTaskStatisticsOnStatusUpdate(TaskStatusUpdatedEvent event) {
        LocalDate today = LocalDate.now();
        
        Optional<TaskDocument> taskOpt = taskDocumentRepository.findByTaskId(event.id());
        if (taskOpt.isPresent()) {
            TaskDocument task = taskOpt.get();
            TaskStatus oldStatus = task.getStatus();
            
            if (task.getAssigneeIds() != null) {
                for (String assigneeId : task.getAssigneeIds()) {
                    updateUserTaskStatistics(assigneeId, today, stats -> {
                        // Уменьшаем счетчик старого статуса
                        if (oldStatus == TaskStatus.COMPLETED) {
                            stats.setCompletedTasks(Math.max(0, stats.getCompletedTasks() - 1));
                        } else if (oldStatus == TaskStatus.IN_PROGRESS) {
                            stats.setInProgressTasks(Math.max(0, stats.getInProgressTasks() - 1));
                        } else if (oldStatus == TaskStatus.AVAILABLE) {
                            stats.setPendingTasks(Math.max(0, stats.getPendingTasks() - 1));
                        }
                        
                        // Увеличиваем счетчик нового статуса
                        if (event.status() == TaskStatus.COMPLETED) {
                            stats.setCompletedTasks(stats.getCompletedTasks() + 1);
                        } else if (event.status() == TaskStatus.IN_PROGRESS) {
                            stats.setInProgressTasks(stats.getInProgressTasks() + 1);
                        } else if (event.status() == TaskStatus.AVAILABLE) {
                            stats.setPendingTasks(stats.getPendingTasks() + 1);
                        }
                    });
                }
            }
        }
    }

    private void updateUserTaskStatistics(String userId, LocalDate date, 
                                         java.util.function.Consumer<UserTaskStatistics> updater) {
        UserTaskStatistics stats = userTaskStatisticsRepository.findByUserIdAndDate(userId, date)
                .orElse(UserTaskStatistics.builder()
                        .userId(userId)
                        .date(date)
                        .totalTasks(0L)
                        .completedTasks(0L)
                        .inProgressTasks(0L)
                        .pendingTasks(0L)
                        .deletedTasks(0L)
                        .completionPercentage(0.0)
                        .build());
        
        updater.accept(stats);
        
        // Пересчитываем процент выполнения
        if (stats.getTotalTasks() > 0) {
            double percentage = (stats.getCompletedTasks() * 100.0) / stats.getTotalTasks();
            stats.setCompletionPercentage(percentage);
        }
        
        // Обновляем распределения
        updateUserTaskStatisticsDistributions(userId, stats);
        
        stats.setLastUpdated(Instant.now());
        userTaskStatisticsRepository.save(stats);
    }

    private void updateUserTaskStatisticsDistributions(String userId, UserTaskStatistics stats) {
        List<TaskDocument> userTasks = taskDocumentRepository.findActiveTasksByAssignee(userId);
        
        Map<String, Long> byCategory = userTasks.stream()
                .filter(t -> t.getDescriptionCategory() != null)
                .collect(Collectors.groupingBy(
                        TaskDocument::getDescriptionCategory,
                        Collectors.counting()));
        
        Map<String, Long> byPriority = userTasks.stream()
                .filter(t -> t.getPriority() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getPriority().name(),
                        Collectors.counting()));
        
        Map<String, Long> byStatus = userTasks.stream()
                .filter(t -> t.getStatus() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getStatus().name(),
                        Collectors.counting()));
        
        Map<String, Long> byDepartment = userTasks.stream()
                .filter(t -> t.getDepartment() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getDepartment().name(),
                        Collectors.counting()));
        
        stats.setTasksByCategory(byCategory);
        stats.setTasksByPriority(byPriority);
        stats.setTasksByStatus(byStatus);
        stats.setTasksByDepartment(byDepartment);
    }

    // Вспомогательный метод для извлечения категории из описания (для пирога)
    private String extractCategory(String title, TaskPriority priority, org.example.events.enums.Department department) {
        if (title == null || title.isEmpty()) {
            return "Без категории";
        }
        
        // Простая категоризация на основе ключевых слов в title
        String lowerTitle = title.toLowerCase();
        
        if (lowerTitle.contains("bug") || lowerTitle.contains("ошибка") || lowerTitle.contains("исправ")) {
            return "Исправление ошибок";
        } else if (lowerTitle.contains("feature") || lowerTitle.contains("функция") || lowerTitle.contains("новый")) {
            return "Новые функции";
        } else if (lowerTitle.contains("refactor") || lowerTitle.contains("рефактор")) {
            return "Рефакторинг";
        } else if (lowerTitle.contains("test") || lowerTitle.contains("тест")) {
            return "Тестирование";
        } else if (lowerTitle.contains("doc") || lowerTitle.contains("документ")) {
            return "Документация";
        } else if (priority == TaskPriority.HIGH) {
            return "Срочные задачи";
        } else {
            return "Прочие задачи";
        }
    }

    // Методы для работы с пользователями (оставляем как есть)
    public void handleUserRegistered(UserCreatedEvent event) {
        log.info("Processing user registered event for user: {}", event.id());

        LocalDate date = LocalDate.now();
        UserStatistics statistics = userStatisticsRepository.findByDate(date).orElse(null);

        if(statistics == null) {
            statistics = new UserStatistics();
            userStatisticsRepository.save(statistics);
        }

        userStatisticsRepository.incrementNewUsers(date, Instant.now(), 1L);
        incrementLoginCountInDailyActiveUser(date, event.username());

        userCounterRepository.ensureExists();
        userCounterRepository.increment(Instant.now());
        UserCounter counter = userCounterRepository.findByGlobalId();
        Long totalUsers = counter != null ? counter.getTotalUsers() : 0L;
        userStatisticsRepository.setTotalUsers(date, Instant.now(), totalUsers);

        log.debug("Сохранена метрика регистрации пользователя: {}", event.id());
    }

    public void handleUserUpdated(UserProfileUpdatedEvent event) {
        log.info("Processing user updated event for user: {}", event.id());
        log.debug("Сохранена метрика обновления пользователя: {}", event.id());
    }

    public void handleUserLoginSuccess(UserLoginEvent event) {
        log.info("Processing user login success event for user: {}", event.id());

        LocalDate date = LocalDate.now();
        UserStatistics statistics = userStatisticsRepository.findByDate(date).orElse(null);

        if(statistics == null) {
            statistics = new UserStatistics();
            userStatisticsRepository.save(statistics);
        }

        userStatisticsRepository.incrementSuccessfulLogins(date, Instant.now(), 1L);
        incrementLoginCountInDailyActiveUser(date, event.username());

        log.debug("Сохранена метрика успешного входа: {}", event.id());
    }

    public void handleUserLoginFailed(LoginFailEvent eventJson) {
        log.info("Processing user login failed event: {}", eventJson);

        LocalDate date = LocalDate.now();
        UserStatistics statistics = userStatisticsRepository.findByDate(date).orElse(null);

        if(statistics == null) {
            statistics = new UserStatistics();
            userStatisticsRepository.save(statistics);
        }

        userStatisticsRepository.incrementFailedLogins(date, Instant.now(), 1L);
        log.debug("Сохранена метрика неуспешного входа: {}", eventJson.id());
    }

    private void incrementLoginCountInDailyActiveUser(LocalDate date, String username) {
        DailyActiveUser dailyActiveUser = dailyActiveUserRepository.findByDateAndUsername(date, username);

        if(dailyActiveUser == null) {
            dailyActiveUser = new DailyActiveUser();
            dailyActiveUser.setUsername(username);
            dailyActiveUserRepository.save(dailyActiveUser);

            Long countActiveUser = dailyActiveUserRepository.countDistinctByDate(date);
            userStatisticsRepository.setActiveUsers(date, Instant.now(), countActiveUser);
        }

        dailyActiveUserRepository.incrementLoginCount(date, username, 1L);
    }

    // ---------- Вспомогательные методы для аналитики ----------

    private TaskStatistics resolveLatestTaskStats(List<TaskStatistics> statsInRange, LocalDate endDate) {
        if (!statsInRange.isEmpty()) {
            return statsInRange.get(statsInRange.size() - 1);
        }
        return taskStatisticsRepository.findFirstByDateLessThanEqualOrderByDateDesc(endDate).orElse(null);
    }

    private UserTaskStatistics resolveLatestUserStats(String userId, List<UserTaskStatistics> statsInRange, LocalDate endDate) {
        if (!statsInRange.isEmpty()) {
            return statsInRange.get(statsInRange.size() - 1);
        }
        return userTaskStatisticsRepository
                .findFirstByUserIdAndDateLessThanEqualOrderByDateDesc(userId, endDate)
                .orElse(null);
    }

    private Map<LocalDate, Long> buildDailyTaskMap(List<TaskStatistics> stats,
                                                   Function<TaskStatistics, Long> extractor) {
        Map<LocalDate, Long> result = new LinkedHashMap<>();
        stats.stream()
                .sorted(Comparator.comparing(TaskStatistics::getDate))
                .forEach(stat -> result.put(stat.getDate(), Optional.ofNullable(extractor.apply(stat)).orElse(0L)));
        return result;
    }

    private Map<String, Long> aggregateDistributions(List<UserTaskStatistics> stats,
                                                     Function<UserTaskStatistics, Map<String, Long>> extractor) {
        Map<String, Long> aggregated = new LinkedHashMap<>();
        stats.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .forEach(map -> map.forEach((key, value) -> aggregated.merge(key, value, Long::sum)));
        return aggregated;
    }

    private Map<String, Long> copyOrEmpty(Map<String, Long> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }
        return new LinkedHashMap<>(source);
    }

    private long safeLong(Long value) {
        return value != null ? value : 0L;
    }

    private double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private double calculateAverageCompletionTimeHours(LocalDate startDate, LocalDate endDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        return taskDocumentRepository.findAllCompletedTasks().stream()
                .filter(task -> task.getCreatedAt() != null && task.getCompletedAt() != null)
                .filter(task -> isWithinRange(task.getCompletedAt(), startDate, endDate, zoneId))
                .mapToDouble(task -> Duration.between(task.getCreatedAt(), task.getCompletedAt()).toMinutes() / 60.0)
                .filter(value -> value > 0)
                .average()
                .orElse(0.0);
    }

    private boolean isWithinRange(Instant instant, LocalDate startDate, LocalDate endDate, ZoneId zoneId) {
        LocalDate date = instant.atZone(zoneId).toLocalDate();
        return !(date.isBefore(startDate) || date.isAfter(endDate));
    }
}
