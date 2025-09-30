package org.example.analyticsservice.repository;

import org.example.analyticsservice.model.LoginMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LoginMetricsRepository extends JpaRepository<LoginMetrics, Long> {

    List<LoginMetrics> findByUserId(String userId);

    List<LoginMetrics> findByLoginStatus(String loginStatus);

    List<LoginMetrics> findByEventTimestampBetween(Instant start, Instant end);

    @Query("SELECT COUNT(lm) FROM LoginMetrics lm WHERE lm.loginStatus = :status AND lm.eventTimestamp BETWEEN :start AND :end")
    Long countByLoginStatusAndTimeBetween(@Param("status") String status, 
                                         @Param("start") Instant start, 
                                         @Param("end") Instant end);

    @Query("SELECT lm.failureReason, COUNT(lm) FROM LoginMetrics lm WHERE lm.loginStatus = 'FAILED' AND lm.eventTimestamp BETWEEN :start AND :end GROUP BY lm.failureReason")
    List<Object[]> countFailureReasonsByTimeBetween(@Param("start") Instant start, 
                                                   @Param("end") Instant end);

    @Query("SELECT DATE(lm.eventTimestamp), COUNT(lm) FROM LoginMetrics lm WHERE lm.loginStatus = :status AND lm.eventTimestamp BETWEEN :start AND :end GROUP BY DATE(lm.eventTimestamp) ORDER BY DATE(lm.eventTimestamp)")
    List<Object[]> countDailyLoginsByStatusAndTimeBetween(@Param("status") String status, 
                                                         @Param("start") Instant start, 
                                                         @Param("end") Instant end);
}
