package org.example.analyticsservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.config.DataSeeder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seed")
@Slf4j
@RequiredArgsConstructor
@Profile({"dev", "docker"})
public class SeederController {

    private final DataSeeder dataSeeder;

    @GetMapping("/start")
    public ResponseEntity<String> startSeeder() {
        log.info("Получен запрос на запуск Java сидера.");

        try {
            dataSeeder.seedIfEmpty();
            return ResponseEntity.ok("Java сидер успешно запущен и завершен.\nДанные добавлены или обновлены.");
        } catch (Exception e) {
            log.error("Ошибка при попытке запустить Java сидер: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера при запуске сидера: " + e.getMessage());
        }
    }
}


