FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Pproduction,oracle

FROM eclipse-temurin:21-jre-jammy

LABEL maintainer="e-codex@eulisa.europa.eu"
LABEL description="e-CODEX connector"

ARG USERNAME=connector
ARG USER_UID=1000
ARG USER_GID=${USER_UID}
ARG BUILD_OUTPUT_FOLDER=/app/domibusConnectorDistribution/target/domibusConnector
ARG APP_FOLDER=/app

WORKDIR ${APP_FOLDER}

RUN groupadd -g ${USER_GID} ${USERNAME} \
    && useradd -u ${USER_UID} -g ${USER_GID} -m ${USERNAME} \
    && mkdir -p data temp ../logs config \
    && chown -R ${USERNAME}:${USERNAME} ${APP_FOLDER} \
    && chown -R ${USERNAME}:${USERNAME} /logs

COPY --from=build --chown=${USERNAME}:${USERNAME} ${BUILD_OUTPUT_FOLDER}/standalone/bin/ ${APP_FOLDER}/bin/
COPY --from=build --chown=${USERNAME}:${USERNAME} ${BUILD_OUTPUT_FOLDER}/standalone/lib/ ${APP_FOLDER}/lib/
COPY --from=build --chown=${USERNAME}:${USERNAME} ${BUILD_OUTPUT_FOLDER}/standalone/start.sh ${APP_FOLDER}/

RUN chmod +x /app/start.sh

USER $USERNAME

EXPOSE 9081

ENTRYPOINT ["/app/start.sh"]
