plugins {
    id("eu.domibus.connector.java-conventions")
    alias(libs.plugins.lombok)
}

dependencies {
    api(project(":domibusConnectorControllerLIB"))
    api(project(":domibusConnectorControllerAPI"))
    api(libs.eu.ecodex.utils.spring.boot.property.converter)
    api(libs.org.springframework.boot.spring.boot.starter.data.jpa) {
        exclude(group = "ch.qos.logback", module = "logback-classic")
        exclude(group = "org.slf4j", module = "log4j-over-slf4j")
    }
    api(libs.org.springframework.spring.jdbc)
    api(libs.org.springframework.boot.spring.boot.starter.log4j2)
    api(libs.org.springframework.security.spring.security.crypto)
    api(libs.com.google.code.findbugs.jsr305)
    api(libs.org.apache.commons.commons.lang3)
    api(libs.jakarta.validation.jakarta.validation.api)
    runtimeOnly(libs.com.oracle.database.jdbc.ojdbc8)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.com.github.database.rider.rider.spring) {
        exclude(group = "junit", module = "junit")
    }
    testImplementation(libs.org.testcontainers.testcontainers)
    testImplementation(libs.org.testcontainers.mysql)
    testImplementation(libs.org.testcontainers.mariadb)
    testImplementation(libs.org.testcontainers.oracle.xe)
    testImplementation(libs.org.testcontainers.postgresql)
    testImplementation(libs.org.postgresql.postgresql)
    //testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    //testImplementation(libs.org.junit.jupiter.junit.jupiter.params)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)
    testImplementation(libs.com.h2database.h2)
    testImplementation(libs.org.liquibase.liquibase.core)
    testImplementation(libs.com.mysql.mysql.connector.j)
    testImplementation(libs.org.dbunit.dbunit) {
        exclude(group = "junit", module = "junit")
    }
    testImplementation(libs.org.apache.tomcat.tomcat.jdbc)
    testImplementation(project(":domibusConnectorControllerAPI"))
    testImplementation(project(":domibusConnectorTestData"))
}

description = "This module is responsibly for persisting the messages and message contents into a persistent storage (Database)."

