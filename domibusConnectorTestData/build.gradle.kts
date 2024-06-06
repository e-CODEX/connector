plugins {
    id("eu.domibus.connector.java-conventions")
    alias(libs.plugins.lombok)
}

dependencies {
    api(project(":domibusConnectorControllerAPI"))
    api(project(":domibusConnectorAPI"))
    api(project(":domibusConnectorAPIlib"))

    // spring dependencies
    api(libs.org.springframework.spring.context)
    api(libs.com.google.code.findbugs.jsr305)
    api(libs.org.apache.cxf.cxf.core)
    api(libs.org.apache.logging.log4j.log4j.core)

    // test dependencies
    api(libs.org.springframework.boot.spring.boot.starter.test)
    api(libs.org.junit.jupiter.junit.jupiter.api)
    api(libs.org.mockito.mockito.core)
    api(libs.org.hamcrest.hamcrest.core)
    api(libs.org.assertj.assertj.core)
}

description = "This module contains testData for Tests. To make the TestData reusable through the different modules of the project."
val testconfig by tasks.registering(Zip::class) {
    archiveClassifier.set("testconfig")
    from("src/main/env")
    destinationDirectory = base.libsDirectory
}

val testdataDatabaseScripts by tasks.registering(Zip::class) {
    archiveClassifier.set("testdata-database-scripts")
    from("src/main/resources/database-scripts") {
        include("*.sql")
    }
    into("database-scripts")
    destinationDirectory = base.libsDirectory
}

val connectorTestKeyStores by tasks.registering(Zip::class) {
    archiveClassifier.set("connectortestkeystores")
    from("src/main/resources/keystores") {
        include("connector-*.jks")
    }
    into("keystores")
    destinationDirectory = base.libsDirectory
}

val gatewayTestKeyStores by tasks.registering(Zip::class) {
    archiveClassifier.set("gwtestkeystores")
    from("src/main/resources/keystores") {
        include("gw-*.jks")
    }
    into("keystores")
    destinationDirectory = base.libsDirectory
}

val testKeyStores by tasks.registering(Zip::class) {
    archiveClassifier.set("testkeystores")
    from("src/main/resources/keystores") {
        include("*.jks")
    }
    into("keystores")
    destinationDirectory = base.libsDirectory
}

tasks.named("assemble") {
    dependsOn(testconfig, testdataDatabaseScripts, connectorTestKeyStores, gatewayTestKeyStores, testKeyStores)
}

publishing{
    publications {
        (this["maven"] as MavenPublication).apply {
            artifact(testconfig)
            artifact(testdataDatabaseScripts)
            artifact(connectorTestKeyStores)
            artifact(gatewayTestKeyStores)
            artifact(testKeyStores)
        }
    }
}
