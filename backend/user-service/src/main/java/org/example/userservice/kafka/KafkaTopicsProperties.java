package org.example.userservice.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicsProperties {

    private String userCreated;
    private String userProfileUpdate;
    private String userDelete;
}
