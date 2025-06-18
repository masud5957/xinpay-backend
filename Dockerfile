# Stage 1: Build the project
FROM maven:3.9.3-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .

WORKDIR /app/backend
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
