#!/bin/sh

#WORKDIR=./
#CLASSPATH=$WORKDIR/lib/*
#java -Dspring.config.location=$WORKDIR/config -Dspring.config.name=connector -Dspring.cloud.bootstrap.location=$WORKDIR/config/bootstrap.properties -cp $CLASSPATH org.springframework.boot.loader.PropertiesLauncher

java -Dloader.path=./lib -Dspring.config.name=connector -cp ./bin/connector.jar org.springframework.boot.loader.PropertiesLauncher


