package org.example.taskservice.repository;

import org.example.events.enums.Department;
import org.example.events.enums.TaskStatus;
import org.example.events.enums.TaskPriority;
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
import java.util.regex.Pattern;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TaskRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Task> findTasksByFilters(TaskStatus status,
                                         TaskPriority priority,
                                         String assigneeId,
                                         String creatorId,
                                         Department department,
                                         String search,
                                         Pageable pageable) {
        List<Criteria> filters = new ArrayList<>();
        if (status != null) filters.add(Criteria.where("status").is(status));
        if (priority != null) filters.add(Criteria.where("priority").is(priority));
        if (assigneeId != null && !assigneeId.isBlank()) filters.add(Criteria.where("assigneeIds").in(assigneeId));
        if (creatorId != null && !creatorId.isBlank()) filters.add(Criteria.where("creatorId").is(creatorId));
        if (department != null) filters.add(Criteria.where("department").is(department));
        if (search != null && !search.isBlank()) {
            Pattern regex = Pattern.compile(Pattern.quote(search.trim()), Pattern.CASE_INSENSITIVE);
            filters.add(new Criteria().orOperator(
                    Criteria.where("title").regex(regex),
                    Criteria.where("description").regex(regex)
            ));
        }

        Query query = new Query();
        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(Criteria[]::new)));
        }
        query.with(pageable);

        List<Task> content = mongoTemplate.find(query, Task.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Task.class);
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Task> findTasksByAssigneeWithFilters(String assigneeId, TaskStatus status, String creatorId, Department department, Pageable pageable) {
        List<Criteria> filters = new ArrayList<>();
        // Always filter by assigneeId
        filters.add(Criteria.where("assigneeIds").in(assigneeId));
        if (status != null) filters.add(Criteria.where("status").is(status));
        if (creatorId != null && !creatorId.isBlank()) filters.add(Criteria.where("creatorId").is(creatorId));
        if (department != null) filters.add(Criteria.where("department").is(department));

        Criteria criteria = new Criteria().andOperator(filters.toArray(Criteria[]::new));
        Query query = new Query(criteria).with(pageable);

        List<Task> content = mongoTemplate.find(query, Task.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Task.class);
        return new PageImpl<>(content, pageable, total);
    }
}


