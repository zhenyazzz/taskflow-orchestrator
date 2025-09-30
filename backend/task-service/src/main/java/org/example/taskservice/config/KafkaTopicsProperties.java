package org.example.taskservice.config;

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
    private String taskSubscribed;
    private String taskUnsubscribed;
    private String taskCompleted;
    private String taskDeleted;
    private String taskStatusUpdated;
    private String taskAssigneesUpdated;
    private String attachmentCreated;
    private String attachmentDeleted;
}
