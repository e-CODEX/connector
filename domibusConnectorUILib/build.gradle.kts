plugins {
    id("eu.domibus.connector.java-conventions")
    alias(libs.plugins.lombok)
}

description = "This module provides helper classes and methods which can be used in connector ui module."

dependencies {
    api(project(":domibusConnectorControllerAPI"))
    api(project(":domibusConnectorPersistence"))
    implementation (libs.org.springframework.security.spring.security.core)
    implementation (libs.org.springframework.boot.spring.boot.starter.security)
    //implementation (libs.jakarta.jms.api)
    implementation (libs.javax.xml.bind.jaxb.api)
    implementation (libs.jaxb.core)
    implementation (libs.jaxb.impl)
    implementation(libs.org.glassfish.jaxb.jaxb.runtime)
    implementation(libs.jaxb.xjc) // Add this dependency for XJC tool

    compileOnly(libs.javax.servlet.javax.servlet.api)

}

val pmodeXsdResourcesPath = "src/main/resources/pmode/domibus_3_2_4"
val xsdGeneratedOutputDir = layout.buildDirectory.dir("generated-sources/xjc")

val generateJaxb by tasks.register<GenerateJaxbClasses>("generateJaxbClasses") {
    schemaFiles.set(listOf(file("$pmodeXsdResourcesPath/domibus-pmode.xsd")))
    xsdOutputDir.set(xsdGeneratedOutputDir)
    packageName.set("eu.domibus.configuration")
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", xsdGeneratedOutputDir)
        }
    }
}

tasks.named("compileJava") {
    dependsOn(generateJaxb)
}

