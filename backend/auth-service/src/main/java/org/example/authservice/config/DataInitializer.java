package org.example.authservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.repository.UserRepository;
import org.example.events.enums.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Первичное заполнение БД учётными записями при старте приложения.
 * Выполняется только если таблица пользователей пуста.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"dev", "docker"})
public class DataInitializer {

    private static final List<SeedUser> DEFAULT_USERS = List.of(
            new SeedUser(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "admin",
                    "admin@example.com",
                    "Admin123!",
                    Set.of(Role.ROLE_ADMIN, Role.ROLE_USER)
            ),
            new SeedUser(
                    UUID.fromString("22222222-2222-2222-2222-222222222222"),
                    "user",
                    "user@example.com",
                    "User123!",
                    Set.of(Role.ROLE_USER)
            ),
            new SeedUser(
                    UUID.fromString("33333333-3333-3333-3333-333333333333"),
                    "user1",
                    "user1@example.com",
                    "User123!",
                    Set.of(Role.ROLE_USER)
            ),
            new SeedUser(
                    UUID.fromString("44444444-4444-4444-4444-444444444444"),
                    "user2",
                    "user2@example.com",
                    "User123!",
                    Set.of(Role.ROLE_USER)
            ),
            new SeedUser(
                    UUID.fromString("55555555-5555-5555-5555-555555555555"),
                    "user3",
                    "user3@example.com",
                    "User123!",
                    Set.of(Role.ROLE_USER)
            ),
            new SeedUser(
                    UUID.fromString("66666666-6666-6666-6666-666666666666"),
                    "user4",
                    "user4@example.com",
                    "User123!",
                    Set.of(Role.ROLE_USER)
            )
    );

    @Bean
    public CommandLineRunner seedAuthUsers(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> seedIfEmpty(userRepository, passwordEncoder);
    }

    @Transactional
    void seedIfEmpty(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        if (userRepository.count() > 0) {
            log.info("Auth-service: пользователи уже существуют, наполнение пропущено");
            return;
        }

        DEFAULT_USERS.forEach(seed -> {
            // Используем вставку с кастомным ID, чтобы избежать merge/optimistic locking
            userRepository.insertWithCustomId(
                    seed.id(),
                    seed.username(),
                    passwordEncoder.encode(seed.rawPassword()),
                    seed.email()
            );
            seed.roles().forEach(role -> userRepository.insertUserRole(seed.id(), role.name()));
        });

        log.info("Auth-service: создано {} пользователей по умолчанию", DEFAULT_USERS.size());
    }

    private record SeedUser(
            UUID id,
            String username,
            String email,
            String rawPassword,
            Set<Role> roles
    ) {
    }
}

