package org.example.notificationservice.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Slf4j
@Configuration
public class WebClientConfig {
    
    @Value("${app.user-service.url}")
    private String userServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
        .baseUrl(userServiceUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .filter((request, next) -> {
            log.info("Request: {} {}", request.method(), request.url());
            return next.exchange(request);
        })
        .build();
    }
        
}


