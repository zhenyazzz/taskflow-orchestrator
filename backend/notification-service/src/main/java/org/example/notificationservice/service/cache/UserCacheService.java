package org.example.notificationservice.service.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.response.UserDto;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final ReactiveRedisTemplate<String, UserDto> redisTemplate;

    @Value("${app.cache.user-ttl}")
    private long CACHE_TTL_MINUTES;

    @Value("${app.cache.user-key-prefix}")
    private String CACHE_KEY_PREFIX;

    public Mono<Void> cacheUser(String userId, UserDto user) {
        String cacheKey = generateCacheKey(userId);
        return redisTemplate.opsForValue().set(cacheKey, user)
                .then(redisTemplate.expire(cacheKey, Duration.ofMinutes(CACHE_TTL_MINUTES)))
                .doOnSuccess(success -> log.info("User with ID {} cached successfully", userId))
                .doOnError(e -> log.error("Failed to cache user with ID {}: {}", userId, e.getMessage(), e))
                .then();
    }

    public Mono<UserDto> getUserFromCache(String userId) {
        String cacheKey = generateCacheKey(userId);
        return redisTemplate.opsForValue().get(cacheKey)
                .doOnError(e -> log.error("Failed to get user from cache with ID {}: {}", userId, e.getMessage(), e));
    }

    private Mono<Long> deleteUserFromCache(String userId) {
        String cacheKey = generateCacheKey(userId);
        return redisTemplate.delete(cacheKey)
                .doOnSuccess(success -> log.info("User with ID {} deleted from cache", userId))
                .doOnError(e -> log.error("Failed to delete user from cache with ID {}: {}", userId, e.getMessage(), e));
    }

    public Mono<Boolean> refreshUserCache(String userId) {
        String cacheKey = generateCacheKey(userId);
        return redisTemplate.expire(cacheKey, Duration.ofMinutes(CACHE_TTL_MINUTES))
                .doOnSuccess(success -> log.info("User with ID {} refreshed in cache", userId))
                .doOnError(e -> log.error("Failed to refresh user cache with ID {}: {}", userId, e.getMessage(), e));
    }

    public Mono<String> getCachedEmail(String userId) {
        return getUserFromCache(userId)
                .map(UserDto::email)
                .defaultIfEmpty(null);
    }

    private String generateCacheKey(String userId) {
        return CACHE_KEY_PREFIX + userId;
    }
}
