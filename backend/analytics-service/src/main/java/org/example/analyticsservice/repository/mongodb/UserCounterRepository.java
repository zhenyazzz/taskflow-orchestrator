package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.UserCounter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.Instant;

public interface UserCounterRepository extends MongoRepository<UserCounter, String> {
    @Query("{ '_id': 'global' }")
    UserCounter findByGlobalId();

    @Query("{ '_id': 'global' }")
    @Update("{ '$inc': { 'total_users': 1 }, '$set': { 'last_updated': ?0 } }")
    void increment(Instant now);

    @Query("{ '_id': 'global' }")
    @Update("{ '$setOnInsert': { '_id': 'global', 'total_users': 0 } }")
    void ensureExists();
}
