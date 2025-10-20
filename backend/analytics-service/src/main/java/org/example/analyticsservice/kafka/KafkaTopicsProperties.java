package org.example.analyticsservice.kafka;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka.topics")
@Slf4j
public class KafkaTopicsProperties {

    private Map<String, String> topics = new HashMap<>();
    
    public void setTaskCreated(String value) {
        topics.put("task-created", value);
    }
    public void setTaskUpdated(String value) {
        topics.put("task-updated", value);
    }
    public void setTaskDeleted(String value) {
        topics.put("task-deleted", value);
    }
    public void setTaskSubscribed(String value) {
        topics.put("task-subscribed", value);
    }
    public void setTaskUnsubscribed(String value) {
        topics.put("task-unsubscribed", value);
    }
    public void setTaskCompleted(String value) {
        topics.put("task-completed", value);
    }
    
    public void setTaskStatusUpdated(String value) {
        topics.put("task-status-updated", value);
    }

    public void setTaskAssigneesUpdated(String value) {
        topics.put("task-assignees-updated", value);
    }
    
    public void setUserRegistered(String value) {
        topics.put("user-registered", value);
    }
    public void setUserCreated(String value) {
        topics.put("user-created", value);
    }
    public void setUserDeleted(String value) {
        topics.put("user-deleted", value);
    }
    public void setUserUpdated(String value) {
        topics.put("user-updated", value);
    }
    public void setUserLoginSuccess(String value) {
        topics.put("user-login-success", value);
    }
    public void setUserLoginFailed(String value) {
        topics.put("user-login-failed", value);
    }

    @PostConstruct
    public void init() {
        log.info("✅ Kafka topics configuration successfully loaded:");
        if (topics == null || topics.isEmpty()) {
            log.warn("⚠️ Kafka topics map is empty! Проверь YAML конфиг (prefix = kafka.topics)");
        } else {
            topics.forEach((key, value) ->
                    log.info("  → {} = {}", key, value)
            );
        }

        checkTopicExists("task-created", getTaskCreated());
        checkTopicExists("task-updated", getTaskUpdated());
        checkTopicExists("task-deleted", getTaskDeleted());
        checkTopicExists("task-subscribed", getTaskSubscribed());
        checkTopicExists("task-unsubscribed", getTaskUnsubscribed());
        checkTopicExists("task-completed", getTaskCompleted());
        checkTopicExists("task-status-updated", getTaskStatusUpdated());
        checkTopicExists("task-assignees-updated", getTaskAssigneesUpdated());
        checkTopicExists("user-registered", getUserRegistered());
        checkTopicExists("user-created", getUserCreated());
        checkTopicExists("user-deleted", getUserDeleted());
        checkTopicExists("user-updated", getUserUpdated());
        checkTopicExists("user-login-success", getUserLoginSuccess());
        checkTopicExists("user-login-failed", getUserLoginFailed());
    }

    
    public String getTaskCreated() { return topics.get("task-created"); }
    public String getTaskUpdated() { return topics.get("task-updated"); }
    public String getTaskDeleted() { return topics.get("task-deleted"); }
    public String getTaskSubscribed() { return topics.get("task-subscribed"); }
    public String getTaskUnsubscribed() { return topics.get("task-unsubscribed"); }
    public String getTaskCompleted() { return topics.get("task-completed"); }
    public String getTaskStatusUpdated() { return topics.get("task-status-updated"); }
    public String getTaskAssigneesUpdated() { return topics.get("task-assignees-updated"); }
    public String getUserRegistered() { return topics.get("user-registered"); }
    public String getUserUpdated() { return topics.get("user-updated"); }
    public String getUserLoginSuccess() { return topics.get("user-login-success"); }
    public String getUserLoginFailed() { return topics.get("user-login-failed"); }
    public String getUserCreated() { return topics.get("user-created"); }
    public String getUserDeleted() { return topics.get("user-deleted"); }

    private void checkTopicExists(String name, String value) {
        if (value == null) {
            log.warn("⚠️ Топик '{}' не найден в конфиге!", name);
        } else {
            log.debug("Топик '{}' найден: {}", name, value);
        }
    }
}
