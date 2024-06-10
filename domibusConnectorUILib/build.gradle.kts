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
    // todo extract to common place
    implementation(libs.jaxb.xjc) // Add this dependency for XJC tool

    compileOnly(libs.javax.servlet.javax.servlet.api)

}

val pmodeXsdResourcesPath = "src/main/resources/pmode/domibus_3_2_4"
val xsdOutputDir = layout.buildDirectory.dir("generated-sources/xjc")

val prepareDir by tasks.registering {
    doLast {
        if (!xsdOutputDir.get().asFile.exists())
            xsdOutputDir.get().asFile.mkdirs()
    }
}
val generateJaxb by tasks.registering(JavaExec::class) {
    dependsOn(prepareDir)
    group = "build"
    description = "Generates JAXB classes from XSD"
    classpath = configurations["compileClasspath"]
    mainClass.set("com.sun.tools.xjc.XJCFacade")
    args = listOf(
        "-d", xsdOutputDir.get().asFile.absolutePath,
        "-p", "eu.domibus.configuration",
        "-extension",
        "-no-header",
        file("$pmodeXsdResourcesPath/domibus-pmode.xsd").absolutePath
    )
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java", xsdOutputDir)
        }
    }
}

tasks.named("compileJava") {
    dependsOn(generateJaxb)
}

