# ----------- Stage 1: Build the project -----------
FROM maven:3.9.3-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY backend/pom.xml /app/backend/
WORKDIR /app/backend
RUN mvn dependency:go-offline

# Copy source code
COPY backend /app/backend/

# Build the project
RUN mvn clean package -DskipTests

# ----------- Stage 2: Run the app -----------
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
