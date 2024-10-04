FROM eclipse-temurin:21

LABEL maintainer="e-codex@eulisa.europa.eu"
LABEL description="e-CODEX connector"

ARG USERNAME=connector
ARG USER_GROUP=${USERNAME}
ARG DISTRIBUTION_PATH=./domibusConnectorDistribution/target/domibusConnector
ARG APP_FOLDER=/app

WORKDIR ${APP_FOLDER}

COPY ${DISTRIBUTION_PATH}/standalone/bin/ ${APP_FOLDER}/bin/
COPY ${DISTRIBUTION_PATH}/standalone/lib/ ${APP_FOLDER}/lib/
COPY ${DISTRIBUTION_PATH}/standalone/start.sh ${APP_FOLDER}/

RUN groupadd --system ${USER_GROUP} \
    && useradd  --system -s /usr/sbin/nologin -g ${USER_GROUP} ${USERNAME} \
    && mkdir -p data temp ../logs config \
    && chown -R ${USERNAME}:${USER_GROUP} ${APP_FOLDER} \
    && chown -R ${USERNAME}:${USER_GROUP} /logs \
    && rm config/connector.properties

USER $USERNAME

EXPOSE 9081

RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
