package org.example.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        log.info("🚀 [{}] {} {} - Headers: {}", 
                timestamp,
                request.getMethod(), 
                request.getURI(),
                request.getHeaders().toSingleValueMap());

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    ServerHttpResponse response = exchange.getResponse();
                    log.info("✅ [{}] Response: {} - Status: {}", 
                            timestamp,
                            request.getURI(),
                            response.getStatusCode());
                })
                .doOnError(throwable -> {
                    log.error("❌ [{}] Error for {}: {}", 
                            timestamp,
                            request.getURI(),
                            throwable.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
