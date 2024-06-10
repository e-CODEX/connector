plugins {
    id("eu.domibus.connector.java-conventions")
}

dependencies {
    api(project(":domibusConnectorAPI"))
    api(project(":domibusConnectorControllerAPI"))
    api(project(":domibusConnectorController"))
    api(project(":domibusConnectorPersistence"))
    api(libs.org.springframework.boot.spring.boot.starter.actuator)
    api(libs.org.springframework.security.spring.security.web)
    api(libs.org.springframework.security.spring.security.config)
    api(libs.org.springframework.boot.spring.boot.autoconfigure)
    api(libs.io.micrometer.micrometer.registry.prometheus)
    api(libs.org.apache.logging.log4j.log4j.web)
    api(libs.org.mariadb.jdbc.mariadb.java.client)
    api(libs.org.postgresql.postgresql)
    api(libs.com.mysql.mysql.connector.j)
    api(libs.com.h2database.h2)
    testImplementation(libs.org.liquibase.liquibase.core)
    compileOnly(libs.org.springframework.boot.spring.boot.starter.tomcat)
}

description = "domibusConnectorStarter"
