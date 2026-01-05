package org.example.analyticsservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.analyticsservice.model.mongo.*;
import org.example.analyticsservice.repository.mongodb.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDate;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MongoDBInitializer {

    private final MongoTemplate mongoTemplate;
    private final UserCounterRepository userCounterRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final DailyActiveUserRepository dailyActiveUserRepository;
    private final TaskCounterRepository taskCounterRepository;
    private final TaskStatisticsRepository taskStatisticsRepository;

    @Bean
    public ApplicationRunner initializeMongoCollections() {
        return args -> {
            log.info("Initializing MongoDB collections...");
            
            try {
                createCollectionIfNotExists("user_counts");
                createCollectionIfNotExists("user_statistics");
                createCollectionIfNotExists("daily_active_users");
                createCollectionIfNotExists("task_documents");
                createCollectionIfNotExists("task_counts");
                createCollectionIfNotExists("task_statistics");
                createCollectionIfNotExists("user_task_statistics");
                
                createIndexes();
                
                initializeInitialData();
                
                log.info("MongoDB collections initialized successfully");
            } catch (Exception e) {
                log.error("Error initializing MongoDB collections", e);
                throw e;
            }
        };
    }

    private void createCollectionIfNotExists(String collectionName) {
        try {
            if (!mongoTemplate.collectionExists(collectionName)) {
                mongoTemplate.createCollection(collectionName);
                log.info("Created collection: {}", collectionName);
            } else {
                log.info("Collection already exists: {}", collectionName);
            }
        } catch (Exception e) {
            log.error("Error creating collection {}: {}", collectionName, e.getMessage(), e);
            throw e;
        }
    }

    @SuppressWarnings("deprecation")
    private void createIndexes() {
        try {
            IndexOperations userStatsIndexOps = mongoTemplate.indexOps(UserStatistics.class);
            Index userStatsDateIndex = new Index().on("date", Sort.Direction.ASC).unique();
            userStatsIndexOps.ensureIndex(userStatsDateIndex);
            log.info("Created indexes for user_statistics collection");
            
            IndexOperations dailyActiveUsersIndexOps = mongoTemplate.indexOps(DailyActiveUser.class);
            Index dailyActiveUsersIndex = new Index().on("date", Sort.Direction.ASC)
                    .on("username", Sort.Direction.ASC).unique();
            dailyActiveUsersIndexOps.ensureIndex(dailyActiveUsersIndex);
            log.info("Created indexes for daily_active_users collection");
            
            IndexOperations tasksIndexOps = mongoTemplate.indexOps(TaskDocument.class);
            Index taskIdIndex = new Index().on("task_id", Sort.Direction.ASC).unique();
            tasksIndexOps.ensureIndex(taskIdIndex);
            Index creatorIdIndex = new Index().on("creator_id", Sort.Direction.ASC);
            tasksIndexOps.ensureIndex(creatorIdIndex);
            Index assigneeIdsIndex = new Index().on("assignee_ids", Sort.Direction.ASC);
            tasksIndexOps.ensureIndex(assigneeIdsIndex);
            Index isDeletedIndex = new Index().on("is_deleted", Sort.Direction.ASC);
            tasksIndexOps.ensureIndex(isDeletedIndex);
            Index isCompletedIndex = new Index().on("is_completed", Sort.Direction.ASC);
            tasksIndexOps.ensureIndex(isCompletedIndex);
            log.info("Created indexes for task_documents collection");
            
            IndexOperations taskStatsIndexOps = mongoTemplate.indexOps(TaskStatistics.class);
            Index taskStatsDateIndex = new Index().on("date", Sort.Direction.ASC).unique();
            taskStatsIndexOps.ensureIndex(taskStatsDateIndex);
            log.info("Created indexes for task_statistics collection");
            
            IndexOperations userTaskStatsIndexOps = mongoTemplate.indexOps(UserTaskStatistics.class);
            Index userTaskStatsCompositeIndex = new Index().on("user_id", Sort.Direction.ASC)
                    .on("date", Sort.Direction.ASC).unique();
            userTaskStatsIndexOps.ensureIndex(userTaskStatsCompositeIndex);
            Index userIdIndex = new Index().on("user_id", Sort.Direction.ASC);
            userTaskStatsIndexOps.ensureIndex(userIdIndex);
            log.info("Created indexes for user_task_statistics collection");
            
            log.info("Indexes for user_counts and task_counts collections (using _id)");
        } catch (Exception e) {
            log.error("Error creating indexes: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void initializeInitialData() {
        try {
            if (userCounterRepository.count() == 0) {
                UserCounter userCounter = UserCounter.builder()
                        .id("global")
                        .totalUsers(0L)
                        .lastUpdated(Instant.now())
                        .build();
                userCounterRepository.save(userCounter);
                log.info("Initialized UserCounter with id: global");
            }
            
            taskCounterRepository.ensureExists();
            log.info("Initialized TaskCounter with id: global");
            
            LocalDate today = LocalDate.now();
            if (userStatisticsRepository.findByDate(today).isEmpty()) {
                UserStatistics todayStats = UserStatistics.builder()
                        .date(today)
                        .totalUsers(0L)
                        .newUsersToday(0L)
                        .activeUsersToday(0L)
                        .successfulLogins(0L)
                        .failedLogins(0L)
                        .lastUpdated(Instant.now())
                        .build();
                userStatisticsRepository.save(todayStats);
                log.info("Initialized UserStatistics for date: {}", today);
            }
            
            if (taskStatisticsRepository.findByDate(today).isEmpty()) {
                TaskStatistics todayTaskStats = TaskStatistics.builder()
                        .date(today)
                        .totalTasks(0L)
                        .completedTasks(0L)
                        .inProgressTasks(0L)
                        .pendingTasks(0L)
                        .deletedTasks(0L)
                        .createdTasksToday(0L)
                        .completedTasksToday(0L)
                        .deletedTasksToday(0L)
                        .updatedTasksToday(0L)
                        .completionPercentage(0.0)
                        .lastUpdated(Instant.now())
                        .build();
                taskStatisticsRepository.save(todayTaskStats);
                log.info("Initialized TaskStatistics for date: {}", today);
            }
        } catch (Exception e) {
            log.warn("Error initializing initial data (it may already exist): {}", e.getMessage());
        }
    }
}
