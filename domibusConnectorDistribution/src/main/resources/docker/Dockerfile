FROM openjdk:8-jre
MAINTAINER Stephan Spindler <spind42@gmail.com>

COPY maven/ /app/

RUN mkdir /app/logs
RUN mkdir /app/data
RUN chmod 777 /app/start.sh

WORKDIR /app

ENV DB_WAIT false
ENV DB_PORT 3306
ENV DB_HOST DB_HOST

CMD /bin/bash /app/wait-for-it.sh -h ${DB_HOST} -p ${DB_PORT} -t 20 ${WAIT_ENABLED} && /bin/bash /app/start.sh

#ENV SPRING_CLOUD_BOOTSTRAP_LOCATION=/app/config/bootstrap.properties
#ENV SPRING_CONFIG_LOCATION=/app/config/
#ENV SPRING_CONFIG_NAME=connector

ENV SERVER_PORT=8080

EXPOSE 8080
