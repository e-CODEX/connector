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

description = "The evidence toolkit contains the evidence related services. It is responsibly for creating the evidences."



val schemaDirectory = file("src/main/xsd")
val schemaFiles = listOf("eDeliveryDetails.xsd", "TS102640_v2.xsd", "SPOCS_ts102640_soap_body.xsd").map { schemaDirectory.resolve(it) }
val bindingFile = file("src/main/xjb/spocseu.xjb")
val xsdOutputDir = layout.buildDirectory.dir("generated-sources/xjc")

val prepareDir by tasks.registering {
    doLast {
        xsdOutputDir.get().asFile.mkdirs()
    }
}

tasks.register("generateJaxbClasses", JavaExec::class) {
    dependsOn(prepareDir)
    group = "build"
    description = "Generates JAXB classes"
    classpath = configurations["compileClasspath"]
    mainClass.set("com.sun.tools.xjc.XJCFacade")
    args = listOf(
        "-d", xsdOutputDir.get().asFile.absolutePath,
        //"-p", "eu.domibus.configuration",
        "-extension",
        "-no-header",
        "-b", bindingFile.absolutePath,
        *schemaFiles.map { it.absolutePath }.toTypedArray()
    )
}

tasks.named("compileJava") {
    dependsOn("generateJaxbClasses")
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", xsdOutputDir)
        }
    }
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(xsdOutputDir)
}