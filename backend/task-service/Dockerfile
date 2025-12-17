FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Build and install common-kafka-events into the local Maven repo
COPY common-kafka-events/ ./common-kafka-events/
RUN cd common-kafka-events \
    && mvn -q -DskipTests install

# Build auth-service (now that dependency is available)
COPY task-service/ ./task-service/
RUN cd task-service \
    && mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/task-service/target/task-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","app.jar"]