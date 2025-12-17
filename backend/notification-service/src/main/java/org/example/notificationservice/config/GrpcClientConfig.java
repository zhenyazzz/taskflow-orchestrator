package org.example.notificationservice.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcClientConfig {

    @Value("${app.grpc.user-service.host:localhost}")
    private String userServiceHost;

    @Value("${app.grpc.user-service.port:9090}")
    private int userServicePort;

    @Bean(name = "userServiceChannel")
    public ManagedChannel userServiceChannel() {
        log.info("Creating gRPC channel to {}:{}", userServiceHost, userServicePort);
        return ManagedChannelBuilder.forAddress(userServiceHost, userServicePort)
                .usePlaintext() 
                .build();
    }
}

