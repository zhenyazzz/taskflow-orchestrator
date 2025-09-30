package org.example.analyticsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.mapper.LoginMetricsMapper;
import org.example.analyticsservice.mapper.TaskMetricsMapper;
import org.example.analyticsservice.mapper.UserMetricsMapper;
import org.example.analyticsservice.model.LoginMetrics;
import org.example.analyticsservice.model.TaskMetrics;
import org.example.analyticsservice.model.UserMetrics;
import org.example.analyticsservice.repository.LoginMetricsRepository;
import org.example.analyticsservice.repository.TaskMetricsRepository;
import org.example.analyticsservice.repository.UserMetricsRepository;
import org.example.events.task.*;
import org.example.events.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final TaskMetricsRepository taskMetricsRepository;
    private final UserMetricsRepository userMetricsRepository;
    private final LoginMetricsRepository loginMetricsRepository;
    
    private final TaskMetricsMapper taskMetricsMapper;
    private final UserMetricsMapper userMetricsMapper;
    private final LoginMetricsMapper loginMetricsMapper;

    @Transactional
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Processing task created event for task: {}", event.id());
        
        TaskMetrics metrics = taskMetricsMapper.fromTaskCreatedEvent(event);
        taskMetricsRepository.save(metrics);
        log.debug("Сохранена метрика создания задачи: {}", metrics.getId());
    }

    @Transactional
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        log.info("Processing task updated event for task: {}", event.id());
        
        TaskMetrics metrics = taskMetricsMapper.fromTaskUpdatedEvent(event);
        taskMetricsRepository.save(metrics);
        log.debug("Сохранена метрика обновления задачи: {}", metrics.getId());
    }

    @Transactional
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("Processing task completed event for task: {}", event.id());
        
        TaskMetrics metrics = taskMetricsMapper.fromTaskCompletedEvent(event);
        taskMetricsRepository.save(metrics);
        log.debug("Сохранена метрика завершения задачи: {}", metrics.getId());
    }

    @Transactional
    public void handleTaskDeleted(TaskDeletedEvent event) {
        log.info("Processing task deleted event for task: {}", event.id());
        
        TaskMetrics metrics = taskMetricsMapper.fromTaskDeletedEvent(event);
        taskMetricsRepository.save(metrics);
        log.debug("Сохранена метрика удаления задачи: {}", metrics.getId());
    }

    @Transactional
    public void handleUserRegistered(UserCreatedEvent event) {
        log.info("Processing user registered event for user: {}", event.id());
        
        
        UserMetrics metrics = userMetricsMapper.fromUserCreatedEvent(event);
        
        userMetricsRepository.save(metrics);
        log.debug("Сохранена метрика регистрации пользователя: {}", metrics.getId());
    }

    @Transactional
    public void handleUserUpdated(UserProfileUpdatedEvent event) {
        log.info("Processing user updated event for user: {}", event.id());
        
        UserMetrics metrics = userMetricsMapper.fromUserProfileUpdatedEvent(event);
        userMetricsRepository.save(metrics);
        log.debug("Сохранена метрика обновления пользователя: {}", metrics.getId());
    }

    @Transactional
    public void handleUserLoginSuccess(UserLoginEvent event) {
        log.info("Processing user login success event for user: {}", event.id());
        
        LoginMetrics metrics = loginMetricsMapper.fromUserLoginEvent(event);
        loginMetricsRepository.save(metrics);
        log.debug("Сохранена метрика успешного входа: {}", metrics.getId());
    }

    @Transactional
    public void handleUserLoginFailed(LoginFailEvent eventJson) {
        log.info("Processing user login failed event: {}", eventJson);
        
        LoginMetrics metrics = loginMetricsMapper.fromFailedLoginAttempt(eventJson);
        loginMetricsRepository.save(metrics);
        log.debug("Сохранена метрика неуспешного входа: {}", metrics.getId());
    }

    // Дополнительные события задач
    @Transactional
    public void handleTaskStatusUpdated(TaskStatusUpdatedEvent event) {
        log.info("Processing task status updated event for task: {}", event.id());
        
        TaskMetrics metrics = taskMetricsMapper.fromTaskStatusUpdatedEvent(event);
        taskMetricsRepository.save(metrics);
        log.debug("Сохранена метрика обновления статуса задачи: {}", metrics.getId());
    }

    @Transactional
    public void handleTaskAssigneesUpdated(TaskAssigneesUpdatedEvent event) {
        log.info("Processing task assignees updated event for task: {}", event.id());
        
        TaskMetrics metrics = taskMetricsMapper.fromTaskAssigneesUpdatedEvent(event);
        taskMetricsRepository.save(metrics);
        log.debug("Сохранена метрика обновления исполнителей задачи: {}", metrics.getId());
    }

}
