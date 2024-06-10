plugins {
    id("eu.domibus.connector.java-conventions")
}

dependencies {
    api(project(":domibusConnectorAPI"))
    api(libs.org.springframework.spring.core)
    api(libs.javax.validation.validation.api)
    api(libs.org.slf4j.slf4j.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)
    testImplementation(libs.org.hamcrest.hamcrest.core)
    testImplementation(libs.org.assertj.assertj.core)
}

description = "Provides helper classes and methods which can be used in connector, connectorClient and connectorGatewayPlugin"
