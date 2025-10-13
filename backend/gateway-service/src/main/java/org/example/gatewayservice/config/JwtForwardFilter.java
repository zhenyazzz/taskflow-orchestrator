package org.example.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtForwardFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(org.springframework.security.core.Authentication.class)
                .map(authentication -> {
                    // Получаем токен из authentication (мы его сохранили в credentials)
                    String token = (String) authentication.getCredentials();
                    
                    if (token != null) {
                        // Создаем новый request с Authorization header
                        return exchange.mutate()
                                .request(builder -> builder
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                )
                                .build();
                    }
                    return exchange;
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        // Выполняется после аутентификации но до маршрутизации
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}