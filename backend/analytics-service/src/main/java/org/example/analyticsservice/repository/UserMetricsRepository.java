package org.example.analyticsservice.repository;

import org.example.analyticsservice.model.UserMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserMetricsRepository extends JpaRepository<UserMetrics, Long> {

    List<UserMetrics> findByUserId(String userId);

    List<UserMetrics> findByEventType(String eventType);

    List<UserMetrics> findByEventTimestampBetween(Instant start, Instant end);

    @Query("SELECT COUNT(um) FROM UserMetrics um WHERE um.eventType = :eventType AND um.eventTimestamp BETWEEN :start AND :end")
    Long countByEventTypeAndTimeBetween(@Param("eventType") String eventType, 
                                       @Param("start") Instant start, 
                                       @Param("end") Instant end);

    @Query("SELECT um.department, COUNT(um) FROM UserMetrics um WHERE um.eventType = 'REGISTERED' AND um.eventTimestamp BETWEEN :start AND :end GROUP BY um.department")
    List<Object[]> countUsersByDepartmentAndTimeBetween(@Param("start") Instant start, 
                                                       @Param("end") Instant end);

    @Query("SELECT um.role, COUNT(um) FROM UserMetrics um WHERE um.eventType = 'REGISTERED' AND um.eventTimestamp BETWEEN :start AND :end GROUP BY um.role")
    List<Object[]> countUsersByRoleAndTimeBetween(@Param("start") Instant start, 
                                                 @Param("end") Instant end);
}
