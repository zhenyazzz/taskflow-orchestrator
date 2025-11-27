package org.example.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.mapper.LoginMetricsMapper;
import org.example.analyticsservice.mapper.TaskMetricsMapper;
import org.example.analyticsservice.mapper.UserMetricsMapper;
import org.example.analyticsservice.model.LoginMetrics;
import org.example.analyticsservice.model.TaskMetrics;
import org.example.analyticsservice.model.UserMetrics;
import org.example.analyticsservice.model.mongo.DailyActiveUser;
import org.example.analyticsservice.model.mongo.UserStatistics;
import org.example.analyticsservice.repository.LoginMetricsRepository;
import org.example.analyticsservice.repository.TaskMetricsRepository;
import org.example.analyticsservice.repository.UserMetricsRepository;
import org.example.analyticsservice.repository.mongodb.DailyActiveUserRepository;
import org.example.analyticsservice.repository.mongodb.UserCounterRepository;
import org.example.analyticsservice.repository.mongodb.UserStatisticsRepository;
import org.example.events.task.*;
import org.example.events.user.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class AnalyticsService {

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private TaskMetricsRepository taskMetricsRepository;
    
    private final UserStatisticsRepository userStatisticsRepository;
    private final DailyActiveUserRepository dailyActiveUserRepository;
    private final UserCounterRepository userCounterRepository;
    
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private TaskMetricsMapper taskMetricsMapper;
    
    public AnalyticsService(
            UserStatisticsRepository userStatisticsRepository,
            DailyActiveUserRepository dailyActiveUserRepository,
            UserCounterRepository userCounterRepository) {
        this.userStatisticsRepository = userStatisticsRepository;
        this.dailyActiveUserRepository = dailyActiveUserRepository;
        this.userCounterRepository = userCounterRepository;
    }

    @Transactional
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Processing task created event for task: {}", event.id());
        if (taskMetricsRepository != null && taskMetricsMapper != null) {
            TaskMetrics metrics = taskMetricsMapper.fromTaskCreatedEvent(event);
            taskMetricsRepository.save(metrics);
            log.debug("Сохранена метрика создания задачи: {}", metrics.getId());
        } else {
            log.warn("TaskMetricsRepository or TaskMetricsMapper not available, skipping task metrics");
        }
    }

    @Transactional
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        log.info("Processing task updated event for task: {}", event.id());
        if (taskMetricsRepository != null && taskMetricsMapper != null) {
            TaskMetrics metrics = taskMetricsMapper.fromTaskUpdatedEvent(event);
            taskMetricsRepository.save(metrics);
            log.debug("Сохранена метрика обновления задачи: {}", metrics.getId());
        } else {
            log.warn("TaskMetricsRepository or TaskMetricsMapper not available, skipping task metrics");
        }
    }

    @Transactional
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Processing task completed event for task: {}", event.id());
        if (taskMetricsRepository != null && taskMetricsMapper != null) {
            TaskMetrics metrics = taskMetricsMapper.fromTaskCompletedEvent(event);
            taskMetricsRepository.save(metrics);
            log.debug("Сохранена метрика завершения задачи: {}", metrics.getId());
        } else {
            log.warn("TaskMetricsRepository or TaskMetricsMapper not available, skipping task metrics");
        }
    }

    @Transactional
    public void handleTaskDeleted(TaskDeletedEvent event) {
        log.info("Processing task deleted event for task: {}", event.id());
        if (taskMetricsRepository != null && taskMetricsMapper != null) {
            TaskMetrics metrics = taskMetricsMapper.fromTaskDeletedEvent(event);
            taskMetricsRepository.save(metrics);
            log.debug("Сохранена метрика удаления задачи: {}", metrics.getId());
        } else {
            log.warn("TaskMetricsRepository or TaskMetricsMapper not available, skipping task metrics");
        }
    }

    @Transactional
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
        userStatisticsRepository.setTotalUsers(date, Instant.now(), userCounterRepository.getTotalUsers());

        log.debug("Сохранена метрика регистрации пользователя: {}", event.id());
    }

    @Transactional
    public void handleUserUpdated(UserProfileUpdatedEvent event) {
        log.info("Processing user updated event for user: {}", event.id());

        //Not required method - delete in the future

        log.debug("Сохранена метрика обновления пользователя: {}", event.id());
    }

    @Transactional
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

    @Transactional
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

    // Дополнительные события задач
    @Transactional
    public void handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Processing task status updated event for task: {}", event.id());
        if (taskMetricsRepository != null && taskMetricsMapper != null) {
            TaskMetrics metrics = taskMetricsMapper.fromTaskStatusUpdatedEvent(event);
            taskMetricsRepository.save(metrics);
            log.debug("Сохранена метрика обновления статуса задачи: {}", metrics.getId());
        } else {
            log.warn("TaskMetricsRepository or TaskMetricsMapper not available, skipping task metrics");
        }
    }

    @Transactional
    public void handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Processing task assignees updated event for task: {}", event.id());
        if (taskMetricsRepository != null && taskMetricsMapper != null) {
            TaskMetrics metrics = taskMetricsMapper.fromTaskAssigneesUpdatedEvent(event);
            taskMetricsRepository.save(metrics);
            log.debug("Сохранена метрика обновления исполнителей задачи: {}", metrics.getId());
        } else {
            log.warn("TaskMetricsRepository or TaskMetricsMapper not available, skipping task metrics");
        }
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

}
