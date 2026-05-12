FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src src
RUN mvn -q -e -DskipTests package

FROM eclipse-temurin:17-jre-jammy AS runtime
ENV APP_HOME=/app
WORKDIR ${APP_HOME}

RUN useradd -r -u 1001 -g root appuser
COPY --from=builder /workspace/target/*.jar ${APP_HOME}/app.jar
COPY entrypoint.sh ${APP_HOME}/entrypoint.sh
RUN chmod +x ${APP_HOME}/entrypoint.sh

EXPOSE 8080
USER 1001

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["/app/entrypoint.sh"]
