package org.example.userservice.kafka;

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
    
    public void setUserCreated(String value) {
        topics.put("user-created", value);
    }
    public void setUserDeleted(String value) {
        topics.put("user-deleted", value);
    }
    public void setUserProfileUpdated(String value) {
        topics.put("user-profile-updated", value);
    }
    public void setUserRoleUpdated(String value) {
        topics.put("user-role-updated", value);
    }

    public String getUserCreated() { return topics.get("user-created"); }
    public String getUserDeleted() { return topics.get("user-deleted"); }
    public String getUserProfileUpdated() { return topics.get("user-profile-updated"); }
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

        checkTopicExists("user-created", getUserCreated());
        checkTopicExists("user-deleted", getUserDeleted());
        checkTopicExists("user-profile-updated", getUserProfileUpdated());
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
