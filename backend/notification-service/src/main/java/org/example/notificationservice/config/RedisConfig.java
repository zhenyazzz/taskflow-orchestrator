package org.example.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import org.example.notificationservice.dto.response.UserDto;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public ReactiveRedisTemplate<String, UserDto> reactiveRedisTemplate() {
        Jackson2JsonRedisSerializer<UserDto> serializer = 
            new Jackson2JsonRedisSerializer<>(UserDto.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, UserDto> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        
        RedisSerializationContext<String, UserDto> context = 
            builder.value(serializer).build();
        
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(), context);
    }

}
