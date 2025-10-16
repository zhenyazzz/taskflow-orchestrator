package org.example.authservice.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "kafka.topics")
@Slf4j
public class KafkaTopicsProperties {

    private Map<String, String> topics = new HashMap<>();
    
    public void setUserRegistered(String value) {
        topics.put("user-registered", value);
    }
    
    public void setUserLoginSuccess(String value) {
        topics.put("user-login-success", value);
    }
    
    public void setUserLoginFailed(String value) {
        topics.put("user-login-failed", value);
    }
    
    public void setUserRoleUpdated(String value) {
        topics.put("user-role-updated", value);
    }

    public String getUserRegistered() { return topics.get("user-registered"); }
    public String getUserLoginSuccess() { return topics.get("user-login-success"); }
    public String getUserLoginFailed() { return topics.get("user-login-failed"); }
    public String getUserRoleUpdated() { return topics.get("user-role-updated"); }


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

        checkTopicExists("user-registered", getUserRegistered());
        checkTopicExists("user-login-success", getUserLoginSuccess());
        checkTopicExists("user-login-failed", getUserLoginFailed());
        checkTopicExists("user-role-updated", getUserRoleUpdated());
    }

    private void checkTopicExists(String name, String value) {
        if (value == null) {
            log.warn("⚠️ Топик '{}' не найден в конфиге!", name);
        } else {
            log.debug("Топик '{}' найден: {}", name, value);
        }
    }
}

