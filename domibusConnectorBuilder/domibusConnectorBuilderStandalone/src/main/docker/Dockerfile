FROM java:openjdk-8-ubi8
ENV CATALINA_HOME=/app/domibus
ENV DOMIBUS_CONFIG_LOCATION=/app/domibus/conf/domibus
ENV DOMIBUS_WORK_LOCATION=/app/data/work
ENV JAVA_TEMPDIR=/app/temp
ENV JAVA_OPTS="-XX:MetaspaceSize=256M"
COPY app /app/
USER root

RUN mkdir -p /app/config /app/data /app/logs /app/temp /app/lib
#RUN echo "export JAVA_OPTS=\"\$JAVA_OPTS\"" >> /app/run.sh \
RUN echo "exec java \$JAVA_OPTS -Dloader.path=/app/lib -Dspring.config.name=connector -cp /app/connector.jar org.springframework.boot.loader.PropertiesLauncher" >> /app/run.sh

RUN groupadd --system connector && useradd --system -g connector connector && chown -R connector:connector /app
WORKDIR /app
USER connector
EXPOSE 8080
VOLUME /app/config
VOLUME /app/data
VOLUME /app/logs
VOLUME /app/temp
VOLUME /app/lib

CMD ["/bin/sh","/app/run.sh", "run"]