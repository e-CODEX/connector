plugins {
    id("eu.domibus.connector.java-conventions")
}

dependencies {
    api(project(":domibusConnectorControllerAPI"))
    api(project(":domibusConnectorAPIlib"))
    api(libs.eu.ecodex.utils.spring.property.configuration.manager)
    api(libs.eu.ecodex.utils.spring.property.configuration.manager.api)
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.starter.aop)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    api(libs.org.springframework.spring.aspects)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.commons.beanutils.commons.beanutils)
    //api(libs.com.google.code.findbugs.jsr305)
    api("jakarta.validation:jakarta.validation-api:3.1.0")
    api("jakarta.annotation:jakarta.annotation-api:2.1.1")
    api(libs.com.google.guava.guava)
    api(libs.com.google.code.findbugs.annotations)
    api(libs.org.slf4j.slf4j.api)
    api(libs.com.fasterxml.jackson.datatype.jackson.datatype.jsr310)
    testImplementation(project(":domibusConnectorTestData"))
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)
}

description = "Contains common code of all connector modules, like property configuration checkers, helper, ..."


