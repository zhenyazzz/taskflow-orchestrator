package org.example.notificationservice.client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.notificationservice.dto.response.UserDto;
import org.example.notificationservice.service.cache.UserCacheService;


import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient userServiceWebClient;
    private final UserCacheService userCacheService;

    public Mono<UserDto> getUserByIdAsync(String userId) {
        log.debug("Getting user by ID: {}", userId);
        
        return getFromCacheAsync(userId)
            .switchIfEmpty(Mono.defer(() -> getFromServiceAsync(userId)))
            .doOnNext(user -> log.debug("Retrieved user: {}", user.id()))
            .doOnError(error -> log.error("Failed to get user {}: {}", userId, error.getMessage()));
    }

    private Mono<UserDto> getFromCacheAsync(String userId) {
        return userCacheService.getUserFromCache(userId)
            .doOnNext(user -> log.debug("Retrieved user from cache: {}", user.id()));
    }

    private Mono<UserDto> getFromServiceAsync(String userId) {
        log.debug("Fetching user {} from service", userId);
        
        return userServiceWebClient.get()
            .uri("/api/users/{userId}", userId)
            .retrieve()
            .bodyToMono(UserDto.class)
            .timeout(Duration.ofSeconds(10))
            .flatMap(user -> cacheUser(userId, user)
            .thenReturn(user))
            .doOnSuccess(user -> log.debug("Fetched user: {}", user.id()))
            .doOnError(error -> log.error("Failed to fetch user {}: {}", userId, error.getMessage()));
    }

    private Mono<Void> cacheUser(String userId, UserDto user) {
        return userCacheService.cacheUser(userId, user);
    }
}