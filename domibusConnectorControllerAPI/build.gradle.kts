plugins {
    id("eu.domibus.connector.java-conventions")
    `java-test-fixtures`
    alias(libs.plugins.lombok)
}

dependencies {
    api(project(":domibusConnectorAPI"))
    api(libs.org.springframework.spring.core)
    api(libs.org.springframework.boot.spring.boot.starter.log4j2)
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.spring.beans)
    api(libs.org.springframework.data.spring.data.commons)
    api(libs.javax.validation.validation.api)
    api(libs.com.google.code.findbugs.jsr305)
    api(libs.org.apache.commons.commons.lang3)
    api(libs.com.fasterxml.jackson.core.jackson.databind)
    api(libs.javax.javaee.api)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
//    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
//    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)
//    testImplementation(libs.org.hamcrest.hamcrest.core)
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.apache.cxf.cxf.core)
}

description = "This module provides the internal connector interfaces for binding a GatewayLink-Module or a BackendLink module to the connectorModule"

tasks.processResources {
    // Add regular resources
    from("src/main/resources")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE


    // Add filtered resources
    val filteredResources = project.fileTree("src/main/resources-filtered")
    filteredResources.filter { it.isFile }.forEach { file ->
        from(file) {
            // Enable resource filtering for filtered resources
            filter(org.apache.tools.ant.filters.ReplaceTokens::class.java)
        }
    }
}