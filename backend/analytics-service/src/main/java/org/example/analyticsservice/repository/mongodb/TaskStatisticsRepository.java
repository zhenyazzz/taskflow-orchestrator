package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.TaskStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatisticsRepository extends MongoRepository<TaskStatistics, String> {

    Optional<TaskStatistics> findByDate(LocalDate date);

    List<TaskStatistics> findByDateBetween(LocalDate start, LocalDate end);

    Optional<TaskStatistics> findFirstByDateLessThanEqualOrderByDateDesc(LocalDate date);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'total_tasks': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementTotalTasks(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'completed_tasks': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementCompletedTasks(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'in_progress_tasks': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementInProgressTasks(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'pending_tasks': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementPendingTasks(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'deleted_tasks': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementDeletedTasks(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'created_tasks_today': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementCreatedTasksToday(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'completed_tasks_today': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementCompletedTasksToday(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'deleted_tasks_today': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementDeletedTasksToday(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$inc': { 'updated_tasks_today': ?1 }, '$set': { 'last_updated': ?2 } }")
    void incrementUpdatedTasksToday(LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'completion_percentage': ?1, 'last_updated': ?2 } }")
    void updateCompletionPercentage(LocalDate date, Double percentage, Instant lastUpdated);
}


