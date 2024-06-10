import java.util.*

plugins {
    id("eu.domibus.connector.java-conventions")
}

dependencies {
    api(project(":domibusConnectorControllerLIB"))
    api(project(":domibusConnectorControllerAPI"))
    api(project(":domibusConnectorDssToolkit"))
    api(libs.org.springframework.spring.context)
    api(libs.org.springframework.boot.spring.boot)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.apache.logging.log4j.log4j.v1.v2.api)
    api(libs.jakarta.xml.soap.jakarta.xml.soap.api)
    api(libs.javax.xml.bind.jaxb.api)
    api(libs.org.bouncycastle.bcprov.jdk15on)
    api(libs.commons.io.commons.io)
    api(libs.org.bouncycastle.bcmail.jdk15on)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.springframework.spring.test)
    testImplementation(project(":domibusConnectorAPI"))
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.params)
    testImplementation(libs.jaxb.impl)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)

    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.5") // todo extract to common place
    implementation("org.glassfish.jaxb:jaxb-xjc:2.3.5")
}

description =
    "The evidence toolkit contains the evidence related services. It is responsibly for creating the evidences."

val generatedSchemasDir = layout.buildDirectory.dir("generated-sources/xjc")

tasks.register<GenerateJaxbClasses>("generateJaxbClasses") {

    val schemaDirectory = file("src/main/xsd")

    schemaFiles.set(
        listOf(
            "eDeliveryDetails.xsd",
            "TS102640_v2.xsd",
            "SPOCS_ts102640_soap_body.xsd"
        ).map { schemaDirectory.resolve(it) })
    bindingFile.set(file("src/main/xjb/spocseu.xjb"))
    xsdOutputDir.set(generatedSchemasDir)
}

tasks.named("compileJava") {
    dependsOn("generateJaxbClasses")
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", generatedSchemasDir)
        }
    }
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    dependsOn("generateJaxbClasses")
    from(generatedSchemasDir)
}