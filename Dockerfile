# ─── Stage 1: Build ───────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first (layer caching — only re-downloads deps if pom changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ─── Stage 2: Run ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S eduflow && adduser -S eduflow -G eduflow

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown eduflow:eduflow app.jar

# Switch to non-root user
USER eduflow

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
