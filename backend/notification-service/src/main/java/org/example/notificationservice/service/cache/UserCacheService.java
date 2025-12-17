package org.example.notificationservice.service.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.response.UserResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final RedisTemplate<String, UserResponse> redisTemplate;

    @Value("${app.cache.user-ttl}")
    private long cacheTtlMinutes;

    @Value("${app.cache.user-key-prefix}")
    private String cacheKeyPrefix;

    public void cacheUser(String userId, UserResponse user) {
        String cacheKey = generateCacheKey(userId);
        try {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    user,
                    Duration.ofMinutes(cacheTtlMinutes)
            );
            log.info("User with ID {} cached successfully", userId);
        } catch (Exception e) {
            log.error("Failed to cache user with ID {}: {}", userId, e.getMessage(), e);
        }
    }

    public UserResponse getUserFromCache(String userId) {
        String cacheKey = generateCacheKey(userId);
        try {
            return redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.error("Failed to get user from cache with ID {}: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    public Map<String, UserResponse> getUsersFromCache(Set<String> userIds) {
        Map<String, UserResponse> result = new HashMap<>();
    
        for (String id : userIds) {
            UserResponse user = getUserFromCache(id);
            if (user != null) {
                result.put(id, user);
            }
        }
        return result;
    }
    

    public void deleteUserFromCache(String userId) {
        String cacheKey = generateCacheKey(userId);
        try {
            redisTemplate.delete(cacheKey);
            log.info("User with ID {} deleted from cache", userId);
        } catch (Exception e) {
            log.error("Failed to delete user from cache with ID {}: {}", userId, e.getMessage(), e);
        }
    }

    public boolean refreshUserCache(String userId) {
        String cacheKey = generateCacheKey(userId);
        try {
            Boolean result = redisTemplate.expire(
                    cacheKey,
                    Duration.ofMinutes(cacheTtlMinutes)
            );
            log.info("User with ID {} refreshed in cache", userId);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Failed to refresh user cache with ID {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    public Optional<String> getCachedEmail(String userId) {
        UserResponse user = getUserFromCache(userId);
        return Optional.ofNullable(user).map(UserResponse::email);
    }

    private String generateCacheKey(String userId) {
        return cacheKeyPrefix + userId;
    }
}

