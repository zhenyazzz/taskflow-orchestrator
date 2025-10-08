package org.example.analyticsservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicsProperties {

    private String taskCreated;
    private String taskUpdated;
    private String taskCompleted;
    private String taskDeleted;
    private String userRegistered;
    private String userUpdated;
    private String userLoginSuccess;
    private String userLoginFailed;
    private String taskStatusUpdated;
    private String taskAssigneesUpdated;
}
