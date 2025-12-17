package org.example.taskservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.example.taskservice.model.Comment;
import org.example.taskservice.model.Task;
import org.example.taskservice.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Заполняет MongoDB тестовыми задачами при старте, если коллекция пуста.
 * Создателем задач выступает админ, исполнители — созданные ранее пользователи.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"dev", "docker"})
public class DataInitializer {

    private static final String ADMIN_ID = "11111111-1111-1111-1111-111111111111";
    private static final String USER_ID = "22222222-2222-2222-2222-222222222222";
    private static final String USER1_ID = "33333333-3333-3333-3333-333333333333";
    private static final String USER2_ID = "44444444-4444-4444-4444-444444444444";
    private static final String USER3_ID = "55555555-5555-5555-5555-555555555555";
    private static final String USER4_ID = "66666666-6666-6666-6666-666666666666";

    @Bean
    public CommandLineRunner seedTasks(TaskRepository taskRepository) {
        return args -> seedIfEmpty(taskRepository);
    }

    void seedIfEmpty(TaskRepository taskRepository) {
        if (taskRepository.count() > 0) {
            log.info("Task-service: коллекция tasks уже заполнена, наполнение пропущено");
            return;
        }

        Instant now = Instant.now();

        List<TaskSeed> seeds = List.of(
                new TaskSeed(
                        "Настроить CI/CD",
                        "Собрать pipeline GitHub Actions для всех сервисов",
                        TaskStatus.IN_PROGRESS,
                        TaskPriority.HIGH,
                        Department.IT,
                        now.plus(5, ChronoUnit.DAYS),
                        Set.of(USER1_ID, USER2_ID),
                        List.of("ci", "devops"),
                        List.of(
                                new SeedComment("c1", ADMIN_ID, "Создал черновик pipeline", now.minus(3, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS)),
                                new SeedComment("c2", USER1_ID, "Починил шаг юнит-тестов", now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS))
                        ),
                        now.minus(4, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Обновить UI-гайд",
                        "Синхронизировать компоненты с дизайном",
                        TaskStatus.COMPLETED,
                        TaskPriority.MEDIUM,
                        Department.MARKETING,
                        now.minus(1, ChronoUnit.DAYS),
                        Set.of(USER3_ID),
                        List.of("ui", "design-system"),
                        List.of(
                                new SeedComment("c3", USER3_ID, "Добавил новые токены", now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS))
                        ),
                        now.minus(5, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Миграция на Postgres 15",
                        "Перевести все сервисы на новую версию",
                        TaskStatus.BLOCKED,
                        TaskPriority.HIGH,
                        Department.IT,
                        null,
                        Set.of(ADMIN_ID, USER1_ID),
                        List.of("db", "migration"),
                        List.of(
                                new SeedComment("c4", USER1_ID, "Есть блокер по расширению uuid-ossp", now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS))
                        ),
                        now.minus(6, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Тесты для auth-service",
                        "Покрыть контроллеры интеграционными тестами",
                        TaskStatus.IN_PROGRESS,
                        TaskPriority.MEDIUM,
                        Department.IT,
                        now.plus(3, ChronoUnit.DAYS),
                        Set.of(USER_ID),
                        List.of("tests", "auth"),
                        List.of(),
                        now.minus(2, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Мониторинг Prometheus",
                        "Настроить алерты по SLA",
                        TaskStatus.AVAILABLE,
                        TaskPriority.HIGH,
                        Department.IT,
                        now.plus(10, ChronoUnit.DAYS),
                        Set.of(),
                        List.of("monitoring"),
                        List.of(),
                        now.minus(1, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Отчёт по метрикам",
                        "Собрать еженедельный отчёт по MTTR/MTBF",
                        TaskStatus.COMPLETED,
                        TaskPriority.MEDIUM,
                        Department.FINANCE,
                        now.minus(2, ChronoUnit.DAYS),
                        Set.of(USER4_ID),
                        List.of("metrics"),
                        List.of(
                                new SeedComment("c5", USER4_ID, "Отчёт отправлен в почту", now.minus(2, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS))
                        ),
                        now.minus(4, ChronoUnit.DAYS),
                        now.minus(2, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Ретраи в Kafka",
                        "Настроить стратегию повторов для неуспешных событий",
                        TaskStatus.BLOCKED,
                        TaskPriority.HIGH,
                        Department.IT,
                        null,
                        Set.of(USER2_ID),
                        List.of("kafka", "reliability"),
                        List.of(),
                        now.minus(3, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "План обучения команды",
                        "Сформировать план воркшопов по бэкенду",
                        TaskStatus.AVAILABLE,
                        TaskPriority.LOW,
                        Department.HR,
                        now.plus(20, ChronoUnit.DAYS),
                        Set.of(),
                        List.of("training"),
                        List.of(),
                        now.minus(1, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Фича: комментарии в задачах",
                        "Добавить хранение и вывод комментариев",
                        TaskStatus.IN_PROGRESS,
                        TaskPriority.HIGH,
                        Department.IT,
                        now.plus(7, ChronoUnit.DAYS),
                        Set.of(USER3_ID, USER4_ID),
                        List.of("comments", "frontend"),
                        List.of(
                                new SeedComment("c6", ADMIN_ID, "Поставил приоритет высокий", now.minus(2, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS)),
                                new SeedComment("c7", USER3_ID, "Начал работу над схемой", now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS))
                        ),
                        now.minus(3, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                ),
                new TaskSeed(
                        "Бэкап настроек MinIO",
                        "Загрузить конфигурацию в безопасное хранилище",
                        TaskStatus.AVAILABLE,
                        TaskPriority.MEDIUM,
                        Department.IT,
                        now.plus(14, ChronoUnit.DAYS),
                        Set.of(USER1_ID),
                        List.of("backup", "minio"),
                        List.of(
                                new SeedComment("c8", USER1_ID, "Проверил доступы", now.minus(1, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS))
                        ),
                        now.minus(2, ChronoUnit.DAYS),
                        now.minus(1, ChronoUnit.DAYS)
                )
        );

        List<Task> tasks = seeds.stream()
                .map(this::toTask)
                .toList();

        taskRepository.saveAll(tasks);
        log.info("Task-service: создано {} задач по умолчанию", tasks.size());
    }

    private Task toTask(TaskSeed seed) {
        Task task = new Task();
        task.setTitle(seed.title());
        task.setDescription(seed.description());
        task.setStatus(seed.status());
        task.setPriority(seed.priority());
        task.setAssigneeIds(seed.assigneeIds().isEmpty() ? Set.of() : new HashSet<>(seed.assigneeIds()));
        task.setCreatorId(ADMIN_ID);
        task.setDepartment(seed.department());
        task.setCreatedAt(seed.createdAt());
        task.setUpdatedAt(seed.updatedAt());
        task.setDueDate(seed.dueDate());
        task.setTags(seed.tags());

        List<Comment> comments = new ArrayList<>();
        seed.comments().forEach(c -> comments.add(toComment(c)));
        task.setComments(comments);
        return task;
    }

    private Comment toComment(SeedComment c) {
        Comment comment = new Comment();
        comment.setId(c.id());
        comment.setAuthorId(c.authorId());
        comment.setContent(c.content());
        comment.setCreatedAt(c.createdAt());
        comment.setUpdatedAt(c.updatedAt());
        return comment;
    }

    private record SeedComment(
            String id,
            String authorId,
            String content,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    private record TaskSeed(
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            Department department,
            Instant dueDate,
            Set<String> assigneeIds,
            List<String> tags,
            List<SeedComment> comments,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}

