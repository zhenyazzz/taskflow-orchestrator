package org.example.analyticsservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.common.kafka.config.KafkaTopicsProperties;
import org.example.analyticsservice.service.AnalyticsService;
import org.example.events.task.*;
import org.example.events.user.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AnalyticsService analyticsService;
    private final ObjectMapper objectMapper;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    // Task Events
    @KafkaListener(topics = "#{kafkaTopicsProperties.getTaskCreated()}", groupId = "analytics-service-group")
    public void consumeTaskCreated(String eventJson) {
        try {
            TaskCreatedEvent event = objectMapper.readValue(eventJson, TaskCreatedEvent.class);
            log.info("Получено событие TaskCreatedEvent для задачи: {}", event.id());
            analyticsService.handleTaskCreated(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации TaskCreatedEvent: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "#{kafkaTopicsProperties.getTaskUpdated()}", groupId = "analytics-service-group")
    public void consumeTaskUpdated(String eventJson) {
        try {
            TaskUpdatedEvent event = objectMapper.readValue(eventJson, TaskUpdatedEvent.class);
            log.info("Получено событие TaskUpdatedEvent для задачи: {}", event.id());
            analyticsService.handleTaskUpdated(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации TaskUpdatedEvent: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "#{kafkaTopicsProperties.getTaskCompleted()}", groupId = "analytics-service-group")
    public void consumeTaskCompleted(String eventJson) {
        try {
            TaskCompletedEvent event = objectMapper.readValue(eventJson, TaskCompletedEvent.class);
            log.info("Получено событие TaskCompletedEvent для задачи: {}", event.id());
            analyticsService.handleTaskCompleted(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации TaskCompletedEvent: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "#{kafkaTopicsProperties.getTaskDeleted()}", groupId = "analytics-service-group")
    public void consumeTaskDeleted(String eventJson) {
        try {
            TaskDeletedEvent event = objectMapper.readValue(eventJson, TaskDeletedEvent.class);
            log.info("Получено событие TaskDeletedEvent для задачи: {}", event.id());
            analyticsService.handleTaskDeleted(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации TaskDeletedEvent: {}", eventJson, e);
        }
    }

    // User Events
    @KafkaListener(topics = "#{kafkaTopicsProperties.getUserRegistered()}", groupId = "analytics-service-group")
    public void consumeUserRegistered(String eventJson) {
        try {
            UserCreatedEvent event = objectMapper.readValue(eventJson, UserCreatedEvent.class);
            log.info("Получено событие UserCreatedEvent для пользователя: {}", event.id());
            analyticsService.handleUserRegistered(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации UserCreatedEvent: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "#{kafkaTopicsProperties.getUserUpdated()}", groupId = "analytics-service-group")
    public void consumeUserUpdated(String eventJson) {
        try {
            UserProfileUpdatedEvent event = objectMapper.readValue(eventJson, UserProfileUpdatedEvent.class);
            log.info("Получено событие UserProfileUpdatedEvent для пользователя: {}", event.id());
            analyticsService.handleUserUpdated(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации UserProfileUpdatedEvent: {}", eventJson, e);
        }
    }

    // Auth Events
    @KafkaListener(topics = "#{kafkaTopicsProperties.getUserLoginSuccess()}", groupId = "analytics-service-group")
    public void consumeUserLoginSuccess(String eventJson) {
        try {
            UserLoginEvent event = objectMapper.readValue(eventJson, UserLoginEvent.class);
            log.info("Получено событие UserLoginEvent (успешный) для пользователя: {}", event.id());
            analyticsService.handleUserLoginSuccess(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации UserLoginEvent: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "#{kafkaTopicsProperties.getUserLoginFailed()}", groupId = "analytics-service-group")
    public void consumeUserLoginFailed(String eventJson) {

        try {
            LoginFailEvent loginFailEvent = objectMapper.readValue(eventJson, LoginFailEvent.class);
            log.info("Получено событие неуспешного логина: {}", eventJson);
            analyticsService.handleUserLoginFailed(loginFailEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    // Дополнительные события задач
    @KafkaListener(topics = "#{kafkaTopicsProperties.getTaskStatusUpdated()}", groupId = "analytics-service-group")
    public void consumeTaskStatusUpdated(String eventJson) {
        try {
            TaskStatusUpdatedEvent event = objectMapper.readValue(eventJson, TaskStatusUpdatedEvent.class);
            log.info("Получено событие TaskStatusUpdatedEvent для задачи: {}", event.id());
            analyticsService.handleTaskStatusUpdated(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации TaskStatusUpdatedEvent: {}", eventJson, e);
        }
    }

    @KafkaListener(topics = "#{kafkaTopicsProperties.getTaskAssigneesUpdated()}", groupId = "analytics-service-group")
    public void consumeTaskAssigneesUpdated(String eventJson) {
        try {
            TaskAssigneesUpdatedEvent event = objectMapper.readValue(eventJson, TaskAssigneesUpdatedEvent.class);
            log.info("Получено событие TaskAssigneesUpdatedEvent для задачи: {}", event.id());
            analyticsService.handleTaskAssigneesUpdated(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка десериализации TaskAssigneesUpdatedEvent: {}", eventJson, e);
        }
    }
}
