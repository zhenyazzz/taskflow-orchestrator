package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.UserStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatisticsRepository extends MongoRepository<UserStatistics, String> {

    Optional<UserStatistics> findByDate(LocalDate date);

    List<UserStatistics> findByDateBetween(LocalDate start, LocalDate end);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'last_updated': ?1 }, '$inc': { 'successful_logins': ?2 } }")
    void incrementSuccessfulLogins(LocalDate date, java.time.Instant lastUpdated, Long count);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'last_updated': ?1 }, '$inc': { 'failed_logins': ?2 } }")
    void incrementFailedLogins(LocalDate date, java.time.Instant lastUpdated, Long count);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'last_updated': ?1 }, '$inc': { 'new_users_today': ?2 } }")
    void incrementNewUsers(LocalDate date,java.time.Instant lastUpdated, Long count);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'last_updated': ?1 }, '$inc': { 'active_users_today': ?2 } }")
    void incrementActiveUsers(LocalDate date,java.time.Instant lastUpdated, Long count);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'last_updated': ?1 , 'active_users_today': ?2} }")
    void setActiveUsers(LocalDate date,java.time.Instant lastUpdated, Long count);

    @Query("{ 'date': ?0 }")
    @Update("{ '$set': { 'last_updated': ?1 , 'totalUsers': ?2} }")
    void setTotalUsers(LocalDate date,java.time.Instant lastUpdated, Long totalUsers);

}
