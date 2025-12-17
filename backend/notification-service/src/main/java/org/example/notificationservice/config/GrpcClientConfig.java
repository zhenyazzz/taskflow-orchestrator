package org.example.notificationservice.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.user-service.host}")
    private String host;

    @Value("${grpc.user-service.port}")
    private int port;

    private ManagedChannel channel;

    @Bean
    @Qualifier("userServiceChannel")
    public ManagedChannel userServiceChannel() {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        return channel;
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}


