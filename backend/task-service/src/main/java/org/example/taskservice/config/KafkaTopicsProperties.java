package org.example.userservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicsProperties {

    private String userRegistration;
    private String userLogin;
    private String userRoleUpdate;
    private String userCreated;
    private String userProfileUpdate;
    private String userDelete;
}
