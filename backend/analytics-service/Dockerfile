# Multi-stage build для оптимизации размера образа
FROM openjdk:21-jdk-slim as builder

WORKDIR /app

# Копируем Maven wrapper и pom.xml для кеширования зависимостей
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Копируем common-kafka-events для сборки
COPY ../common-kafka-events ../common-kafka-events

# Устанавливаем зависимости
RUN ./mvnw dependency:go-offline

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Финальный образ
FROM openjdk:21-jdk-slim

WORKDIR /app

# Создаем пользователя для безопасности
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Копируем JAR из builder stage
COPY --from=builder /app/target/analytics-service-*.jar app.jar

# Настройки JVM для контейнера
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Экспонируем порт
EXPOSE 8084

# Запускаем приложение с Docker профилем
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]
