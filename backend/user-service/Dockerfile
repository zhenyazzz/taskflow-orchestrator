FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Build and install common-kafka-events into the local Maven repo
COPY common-kafka-events/ ./common-kafka-events/
RUN cd common-kafka-events \
    && mvn -q -DskipTests install

# Build and install common-grpc into the local Maven repo
COPY common-grpc/ ./common-grpc/
RUN cd common-grpc \
    && mvn -q -DskipTests install

# Build user-service (now that dependencies are available)
COPY user-service/ ./user-service/
RUN cd user-service \
    && mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/user-service/target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]