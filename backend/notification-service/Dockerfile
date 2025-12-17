FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY common-grpc/ ./common-grpc/
RUN cd common-grpc \
    && mvn -q clean install -DskipTests

COPY common-kafka-events/ ./common-kafka-events/
RUN cd common-kafka-events \
    && mvn -q clean install -DskipTests

COPY notification-service/ ./notification-service/
RUN cd notification-service \
    && mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /workspace/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]
