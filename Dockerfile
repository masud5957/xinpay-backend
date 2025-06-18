# ----------- Stage 1: Build the project -----------
FROM maven:3.9.3-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy only necessary files first to leverage Docker cache
COPY backend/pom.xml backend/mvnw backend/.mvn /app/backend/

# Pre-download dependencies
WORKDIR /app/backend
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy the full source code after caching dependencies
COPY . .

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

# ----------- Stage 2: Run the application -----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=builder /app/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port for Spring Boot
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
