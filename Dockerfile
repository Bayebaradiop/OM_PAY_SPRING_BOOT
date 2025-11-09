# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/om_pay-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will override with $PORT)
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
