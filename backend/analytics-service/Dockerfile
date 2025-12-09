FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Build and install common-kafka-events into the local Maven repo
COPY common-kafka-events/ ./common-kafka-events/
RUN cd common-kafka-events \
    && mvn -q -DskipTests install

# Build analytics-service (now that dependency is available)
COPY analytics-service/ ./analytics-service/
RUN cd analytics-service \
    && mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Создаем пользователя для безопасности
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Копируем JAR из builder stage
COPY --from=builder --chown=spring:spring /workspace/analytics-service/target/analytics-service-*.jar app.jar

# Настройки JVM для контейнера
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Экспонируем порт
EXPOSE 8084

# Запускаем приложение с Docker профилем
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]