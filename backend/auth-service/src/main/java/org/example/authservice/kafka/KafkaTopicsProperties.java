package org.example.authservice.kafka;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
@Getter
@Setter
public class KafkaTopicsProperties {
    
    private String userRegistered;
    private String userLogin;
    private String userRoleUpdate;
    private String userLoginFailed;
    private String userLogout;
    
}
