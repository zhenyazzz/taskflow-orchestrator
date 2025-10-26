package org.example.taskservice.repository;

import org.example.events.enums.Department;
import org.example.events.enums.TaskStatus;
import org.example.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TaskRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Task> findTasksByFilters(TaskStatus status, String assigneeId, String creatorId, Department department, Pageable pageable) {
        List<Criteria> filters = new ArrayList<>();
        if (status != null) filters.add(Criteria.where("status").is(status));
        if (assigneeId != null && !assigneeId.isBlank()) filters.add(Criteria.where("assigneeIds").is(assigneeId));
        if (creatorId != null && !creatorId.isBlank()) filters.add(Criteria.where("creatorId").is(creatorId));
        if (department != null) filters.add(Criteria.where("department").is(department));

        Criteria criteria = filters.isEmpty() ? new Criteria() : new Criteria().andOperator(filters.toArray(Criteria[]::new));
        Query query = new Query(criteria).with(pageable);

        List<Task> content = mongoTemplate.find(query, Task.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Task.class);
        return new PageImpl<>(content, pageable, total);
    }
}


