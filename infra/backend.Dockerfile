# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

# 1) Stable files first for better cache hits when only source code changes.
COPY gradlew ./gradlew
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./

RUN chmod +x ./gradlew

# 2) Keep downloaded dependencies in this layer so the exported build cache includes them.
RUN ./gradlew --no-daemon downloadDependencies

# 3) Copy application source and build boot jar.
COPY src ./src

RUN ./gradlew --no-daemon -x test -x asciidoctor bootJar

RUN JAR_FILE="$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" \
    && test -n "${JAR_FILE}" \
    && cp "${JAR_FILE}" /workspace/app.jar 
# todo: 이거 자체를 그냥 app.jar로 빌드하게 gradle 수정하자.

FROM eclipse-temurin:21-jre-jammy AS run

WORKDIR /app

RUN useradd --system --uid 10001 appuser

COPY --from=builder /workspace/app.jar ./app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
