# Imagen solo de ejecución (JRE). El JAR se compila ANTES (en local o en el job de CI)
# y aquí solo se copia: la imagen lleva exactamente el artefacto que pasó las pruebas.
FROM eclipse-temurin:25-jre-alpine

# SHA del commit: el pipeline lo pasa con --build-arg y queda grabado en la imagen
ARG GIT_SHA=local
ENV APP_COMMIT=${GIT_SHA}

# La JVM usa como máximo el 75% de la RAM del contenedor (Render free = 512 MB)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"

# No ejecutar como root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
