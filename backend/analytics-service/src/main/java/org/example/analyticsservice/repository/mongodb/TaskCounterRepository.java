package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.TaskCounter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TaskCounterRepository extends MongoRepository<TaskCounter, String> {

    Optional<TaskCounter> findById(String id);

    @Query("{ 'id': 'global' }")
    TaskCounter findByGlobalId();

    @Query("{ 'id': 'global' }")
    @Update("{ '$inc': { 'total_tasks': ?0 }, '$set': { 'last_updated': ?1 } }")
    void increment(Long increment, Instant lastUpdated);

    @Query("{ 'id': 'global' }")
    @Update("{ '$inc': { 'total_tasks': -1 }, '$set': { 'last_updated': ?0 } }")
    void decrement(Instant lastUpdated);

    default void ensureExists() {
        if (findByGlobalId() == null) {
            TaskCounter counter = TaskCounter.builder()
                    .id("global")
                    .totalTasks(0L)
                    .lastUpdated(Instant.now())
                    .build();
            save(counter);
        }
    }
}

