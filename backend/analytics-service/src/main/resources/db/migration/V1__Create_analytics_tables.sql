-- Create analytics database tables

-- Task events table (Event Sourcing approach)
CREATE TABLE task_events (
    id BIGSERIAL PRIMARY KEY,
    task_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB, -- полные данные события
    title VARCHAR(500), -- для быстрых запросов
    priority VARCHAR(20), -- для быстрых запросов
    status VARCHAR(20), -- для быстрых запросов
    assignee_ids TEXT, -- для быстрых запросов
    creator_id VARCHAR(255), -- для быстрых запросов
    completion_time TIMESTAMP,
    created_at TIMESTAMP,
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User metrics table
CREATE TABLE user_metrics (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    username VARCHAR(255),
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    department VARCHAR(50),
    role VARCHAR(50),
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Login metrics table
CREATE TABLE login_metrics (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    username VARCHAR(255),
    email VARCHAR(255),
    login_status VARCHAR(20) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    failure_reason VARCHAR(500),
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_task_events_task_id ON task_events(task_id);
CREATE INDEX idx_task_events_event_type ON task_events(event_type);
CREATE INDEX idx_task_events_event_timestamp ON task_events(event_timestamp);
CREATE INDEX idx_task_events_creator_id ON task_events(creator_id);

CREATE INDEX idx_task_events_assignee_ids ON task_events(assignee_ids);


CREATE INDEX idx_task_events_event_data ON task_events(event_data);

CREATE INDEX idx_user_metrics_user_id ON user_metrics(user_id);
CREATE INDEX idx_user_metrics_event_type ON user_metrics(event_type);
CREATE INDEX idx_user_metrics_event_timestamp ON user_metrics(event_timestamp);

CREATE INDEX idx_login_metrics_user_id ON login_metrics(user_id);
CREATE INDEX idx_login_metrics_login_status ON login_metrics(login_status);
CREATE INDEX idx_login_metrics_event_timestamp ON login_metrics(event_timestamp);
