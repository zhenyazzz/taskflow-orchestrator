package org.example.analyticsservice.mapper;

import org.example.analyticsservice.model.TaskMetrics;
import org.example.events.task.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {ObjectMapper.class})
public interface TaskMetricsMapper {

    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "CREATED")
    @Mapping(source = "assigneeIds", target = "assigneeIds")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    TaskMetrics fromTaskCreatedEvent(TaskCreatedEvent event);

    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "UPDATED")
    @Mapping(source = "assigneeIds", target = "assigneeIds")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskUpdatedEvent(TaskUpdatedEvent event);

    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "COMPLETED")
    @Mapping(source = "completedAt", target = "completionTime")
    @Mapping(source = "assigneeIds", target = "assigneeIds")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskCompletedEvent(TaskCompletedEvent event);

    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "DELETED")
    @Mapping(source = "assigneeIds", target = "assigneeIds")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskDeletedEvent(TaskDeletedEvent event);

    // Добавляем новые события
    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "STATUS_UPDATED")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "assigneeIds", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskStatusUpdatedEvent(TaskStatusUpdatedEvent event);

    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "ASSIGNEES_UPDATED")
    @Mapping(source = "assigneeIds", target = "assigneeIds")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskAssigneesUpdatedEvent(TaskAssigneesUpdatedEvent event);


    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "SUBSCRIBED")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assigneeIds", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskSubscribedEvent(TaskSubscribedEvent event);


    @Mapping(source = "id", target = "taskId")
    @Mapping(target = "eventType", constant = "UNSUBSCRIBED")
    @Mapping(source = "event", target = "eventData", qualifiedByName = "eventToJson")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTimestamp", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assigneeIds", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskMetrics fromTaskUnsubscribedEvent(TaskUnsubscribedEvent event);


    @Named("eventToJson")
    default String eventToJson(Object event) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); 
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
