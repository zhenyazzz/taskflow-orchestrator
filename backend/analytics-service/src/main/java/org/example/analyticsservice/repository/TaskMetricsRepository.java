package org.example.analyticsservice.repository;

import org.example.analyticsservice.model.TaskMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskMetricsRepository extends JpaRepository<TaskMetrics, Long> {

    List<TaskMetrics> findByTaskId(String taskId);

    List<TaskMetrics> findByEventType(String eventType);

    List<TaskMetrics> findByCreatorId(String creatorId);

    List<TaskMetrics> findByAssigneeId(String assigneeId);

    List<TaskMetrics> findByEventTimestampBetween(Instant start, Instant end);

    @Query("SELECT COUNT(tm) FROM TaskMetrics tm WHERE tm.eventType = :eventType AND tm.eventTimestamp BETWEEN :start AND :end")
    Long countByEventTypeAndTimeBetween(@Param("eventType") String eventType, 
                                       @Param("start") Instant start, 
                                       @Param("end") Instant end);

    @Query("SELECT tm.priority, COUNT(tm) FROM TaskMetrics tm WHERE tm.eventType = 'CREATED' AND tm.eventTimestamp BETWEEN :start AND :end GROUP BY tm.priority")
    List<Object[]> countTasksByPriorityAndTimeBetween(@Param("start") Instant start, 
                                                     @Param("end") Instant end);

    @Query("SELECT tm.status, COUNT(tm) FROM TaskMetrics tm WHERE tm.eventType = 'UPDATED' AND tm.eventTimestamp BETWEEN :start AND :end GROUP BY tm.status")
    List<Object[]> countTasksByStatusAndTimeBetween(@Param("start") Instant start, 
                                                   @Param("end") Instant end);

    @Query("SELECT AVG(EXTRACT(EPOCH FROM (tm2.eventTimestamp - tm1.eventTimestamp))) " +
           "FROM TaskMetrics tm1, TaskMetrics tm2 " +
           "WHERE tm1.taskId = tm2.taskId " +
           "AND tm1.eventType = 'CREATED' " +
           "AND tm2.eventType = 'COMPLETED' " +
           "AND tm1.eventTimestamp BETWEEN :start AND :end")
    Double getAverageCompletionTimeInSeconds(@Param("start") Instant start, 
                                           @Param("end") Instant end);
}
