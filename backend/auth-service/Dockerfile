FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests -pl common-kafka-events -am && \
    ./mvnw clean package -DskipTests -pl auth-service -am

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]