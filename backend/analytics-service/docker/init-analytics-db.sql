-- Создание базы данных и пользователя для Analytics Service
CREATE DATABASE analytics_db;
CREATE USER analytics_user WITH ENCRYPTED PASSWORD 'analytics_pass';
GRANT ALL PRIVILEGES ON DATABASE analytics_db TO analytics_user;
