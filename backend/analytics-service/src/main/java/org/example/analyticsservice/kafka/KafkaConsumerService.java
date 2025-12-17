package org.example.analyticsservice.kafka;
    
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.example.analyticsservice.service.AnalyticsService;
import org.example.events.task.*;
import org.example.events.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @Autowired(required = false)
    private AnalyticsService analyticsService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Task Events
    @KafkaListener(topics = "${app.kafka.topics.task-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskCreated(String message) {
        try {
            TaskCreatedEvent event = objectMapper.readValue(message, TaskCreatedEvent.class);
            log.info("Received TaskCreatedEvent: {}", event);
            if (analyticsService != null) {
                log.info("Получено событие TaskCreatedEvent для задачи: {}", event.id());
                analyticsService.handleTaskCreated(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing TaskCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskUpdated(String message) {
        try {
            TaskUpdatedEvent event = objectMapper.readValue(message, TaskUpdatedEvent.class);
            log.info("Received TaskUpdatedEvent for task: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleTaskUpdated(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing TaskUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-completed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskCompleted(String message) {
        try {
            TaskCompletedEvent event = objectMapper.readValue(message, TaskCompletedEvent.class);
            log.info("Received TaskCompletedEvent for task: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleTaskCompleted(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing TaskCompletedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskDeleted(String message) {
        try {
            TaskDeletedEvent event = objectMapper.readValue(message, TaskDeletedEvent.class);
            log.info("Received TaskDeletedEvent for task: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleTaskDeleted(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing TaskDeletedEvent: {}", e.getMessage(), e);
        }
    }

    // User Events
    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegistered(String message) {
        try {
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
            log.info("Received UserCreatedEvent for user: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleUserRegistered(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing UserCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserUpdated(String message) {
        try {
            UserProfileUpdatedEvent event = objectMapper.readValue(message, UserProfileUpdatedEvent.class);
            log.info("Received UserProfileUpdatedEvent for user: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleUserUpdated(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing UserProfileUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    // Auth Events
    @KafkaListener(topics = "${app.kafka.topics.user-login-success}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLoginSuccess(String message) {
        try {
            UserLoginEvent event = objectMapper.readValue(message, UserLoginEvent.class);
            log.info("Received UserLoginEvent (successful) for user: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleUserLoginSuccess(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing UserLoginEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.user-login-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserLoginFailed(String message) {
        try {
            LoginFailEvent loginFailEvent = objectMapper.readValue(message, LoginFailEvent.class);
            log.info("Received failed login event: {}", loginFailEvent.failureReason());
            if (analyticsService != null) {
                analyticsService.handleUserLoginFailed(loginFailEvent);
            }
        } catch (Exception e) {
            log.error("Error deserializing LoginFailEvent: {}", e.getMessage(), e);
        }
    }

    // Дополнительные события задач
    @KafkaListener(topics = "${app.kafka.topics.task-status-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskStatusUpdated(String message) {
        try {
            TaskStatusUpdatedEvent event = objectMapper.readValue(message, TaskStatusUpdatedEvent.class);
            log.info("Received TaskStatusUpdatedEvent for task: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleTaskStatusUpdated(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing TaskStatusUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.task-assignees-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskAssigneesUpdated(String message) {
        try {
            TaskAssigneesUpdatedEvent event = objectMapper.readValue(message, TaskAssigneesUpdatedEvent.class);
            log.info("Received TaskAssigneesUpdatedEvent for task: {}", event.id());
            if (analyticsService != null) {
                analyticsService.handleTaskAssigneesUpdated(event);
            }
        } catch (Exception e) {
            log.error("Error deserializing TaskAssigneesUpdatedEvent: {}", e.getMessage(), e);
        }
    }
}
