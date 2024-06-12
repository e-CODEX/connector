# syntax=docker/dockerfile:1

################################################################################

# Create a stage for building the application based on the stage with downloaded dependencies.
FROM eclipse-temurin:21-jdk-jammy as package

WORKDIR /build

COPY . /build/

# Add to the mvnw wrapper executable permissions.
RUN chmod +x mvnw
# Leverage a cache mount to /root/.m2 so that subsequent builds don't have to
# re-download packages.
RUN ls
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw package -pl :domibusConnectorBuilderStandalone -am -DskipTests -Pproduction && \
    mv domibusConnectorBuilder/domibusConnectorBuilderStandalone/target/domibusConnectorBuilderStandalone-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar


################################################################################

# Create a new stage for running the application that contains the minimal
# runtime dependencies for the application.

FROM eclipse-temurin:21-jre-alpine AS final

# Create a non-privileged user that the app will run under.
# See https://docs.docker.com/go/dockerfile-user-best-practices/
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    connector

RUN mkdir -p /app/config /app/data /app/logs /app/temp && chown -R connector:connector /app

USER connector

# Copy the executable from the "package" stage.
COPY --from=package build/target/app.jar /app/connector.jar

VOLUME /app/temp
VOLUME /app/data
VOLUME /app/logs
VOLUME /app/config

EXPOSE 9081

ENTRYPOINT [ "java","-XX:MetaspaceSize=256M", "-Dspring.config.name=connector", "-Dspring.config.location=/app/config/", "-cp", "/app/connector.jar", "org.springframework.boot.loader.PropertiesLauncher" ]
