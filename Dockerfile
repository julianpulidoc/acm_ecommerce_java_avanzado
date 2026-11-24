# Etapa 1: Compilación con JDK 25
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

# Etapa 2: Imagen final liviana de ejecución (JRE 25)
FROM eclipse-temurin:25-jre-alpine

# SHA del commit inyectado por el pipeline CI/CD
ARG GIT_SHA=local
ENV APP_COMMIT=${GIT_SHA}

# La JVM usa como máximo el 75% de la RAM del contenedor (Render free = 512 MB)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

# Usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
