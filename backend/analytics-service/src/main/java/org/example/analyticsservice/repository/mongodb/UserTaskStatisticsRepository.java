package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.UserTaskStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskStatisticsRepository extends MongoRepository<UserTaskStatistics, String> {

    Optional<UserTaskStatistics> findByUserIdAndDate(String userId, LocalDate date);

    @Query("{ 'user_id': ?0 }")
    List<UserTaskStatistics> findByUserId(String userId);

    @Query("{ 'user_id': ?0, 'date': { '$gte': ?1, '$lte': ?2 } }")
    List<UserTaskStatistics> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);

    @Query("{ 'date': ?0 }")
    List<UserTaskStatistics> findByDate(LocalDate date);

    @Query("{ 'date': { '$gte': ?0, '$lte': ?1 } }")
    List<UserTaskStatistics> findByDateBetween(LocalDate start, LocalDate end);

    @Query(value = "{ 'user_id': ?0, 'date': ?1 }", exists = true)
    boolean existsByUserIdAndDate(String userId, LocalDate date);

    Optional<UserTaskStatistics> findFirstByUserIdAndDateLessThanEqualOrderByDateDesc(String userId, LocalDate date);

    @Query("{ 'user_id': ?0, 'date': ?1 }")
    @Update("{ '$inc': { 'total_tasks': ?2 }, '$set': { 'last_updated': ?3 } }")
    void incrementTotalTasks(String userId, LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'user_id': ?0, 'date': ?1 }")
    @Update("{ '$inc': { 'completed_tasks': ?2 }, '$set': { 'last_updated': ?3 } }")
    void incrementCompletedTasks(String userId, LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'user_id': ?0, 'date': ?1 }")
    @Update("{ '$inc': { 'in_progress_tasks': ?2 }, '$set': { 'last_updated': ?3 } }")
    void incrementInProgressTasks(String userId, LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'user_id': ?0, 'date': ?1 }")
    @Update("{ '$inc': { 'pending_tasks': ?2 }, '$set': { 'last_updated': ?3 } }")
    void incrementPendingTasks(String userId, LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'user_id': ?0, 'date': ?1 }")
    @Update("{ '$inc': { 'deleted_tasks': ?2 }, '$set': { 'last_updated': ?3 } }")
    void incrementDeletedTasks(String userId, LocalDate date, Long increment, Instant lastUpdated);

    @Query("{ 'user_id': ?0, 'date': ?1 }")
    @Update("{ '$set': { 'completion_percentage': ?2, 'last_updated': ?3 } }")
    void updateCompletionPercentage(String userId, LocalDate date, Double percentage, Instant lastUpdated);
}

