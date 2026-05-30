# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:24-jdk-alpine AS builder

WORKDIR /app

# Cache Gradle wrapper and dependencies before copying source
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon --quiet

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Run ────────────────────────────────────────────────────────────
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
