package org.example.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.example.notificationservice.dto.response.UserResponse;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserResponse> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, UserResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserResponse.class));

        return template;
    }
}

