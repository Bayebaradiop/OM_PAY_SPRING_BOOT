# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn .mvn

# Build the application
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/om_pay-0.0.1-SNAPSHOT.jar app.jar

# Set environment for production
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
