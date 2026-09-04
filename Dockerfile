# The build stage uses the official Maven image so Maven itself is already present.
# The previous JDK-only base made `./mvnw` download the Maven distribution on every
# build, which is a network round-trip that can (and did) fail and break the deploy.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src/ src/
# The cache mount keeps ~/.m2 between builds, so dependencies are fetched once rather
# than on every build. `dependency:go-offline` is deliberately gone: it is unreliable
# about resolving everything and the caching it was there for is handled here instead.
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# application.yaml and the RSA keys are NOT baked in — see .dockerignore. They are
# mounted at /app/config at runtime, which Spring Boot picks up automatically because
# ./config/ next to the jar is a default config location.
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
