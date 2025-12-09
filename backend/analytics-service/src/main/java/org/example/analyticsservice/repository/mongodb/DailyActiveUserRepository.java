package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.DailyActiveUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.LocalDate;
import java.util.UUID;

public interface DailyActiveUserRepository extends MongoRepository<DailyActiveUser, String> {
    DailyActiveUser findByDateAndUsername(LocalDate date, String username);

    @Query("{ 'date': ?0 , 'username' :  ?1}")
    @Update("{'$inc': { 'loginCount': ?2 } }")
    void incrementLoginCount(LocalDate date, String username, Long count);

    Long countDistinctByDate(LocalDate date);
}
