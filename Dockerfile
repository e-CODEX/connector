FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Pproduction,oracle

FROM eclipse-temurin:21

LABEL maintainer="e-codex@eulisa.europa.eu"
LABEL description="e-CODEX connector"

ARG USERNAME=connector
ARG USER_GROUP=${USERNAME}
ARG DISTRIBUTION_PATH=/app/domibusConnectorDistribution/target/domibusConnector
ARG APP_FOLDER=/app

WORKDIR ${APP_FOLDER}

COPY --from=build ${DISTRIBUTION_PATH}/standalone/bin/ ${APP_FOLDER}/bin/
COPY --from=build ${DISTRIBUTION_PATH}/standalone/lib/ ${APP_FOLDER}/lib/
COPY --from=build ${DISTRIBUTION_PATH}/standalone/start.sh ${APP_FOLDER}/

RUN groupadd --system ${USER_GROUP} \
    && useradd  --system -s /usr/sbin/nologin -g ${USER_GROUP} ${USERNAME} \
    && mkdir -p data temp ../logs config \
    && chown -R ${USERNAME}:${USER_GROUP} ${APP_FOLDER} \
    && chown -R ${USERNAME}:${USER_GROUP} /logs

USER $USERNAME

EXPOSE 9081

RUN chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
