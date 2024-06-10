plugins {
    id("eu.domibus.connector.java-conventions")
    alias(libs.plugins.lombok)
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter)
    api(libs.org.springframework.boot.spring.boot.starter.web.services)
    api(libs.org.springframework.boot.spring.boot.starter.actuator)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    api(libs.org.springframework.spring.jms)
    api(libs.org.springframework.boot.spring.boot.starter.quartz)
    api(libs.org.apache.cxf.cxf.rt.ws.policy)
    api(libs.org.apache.cxf.cxf.rt.ws.security){
        exclude(group = "com.fasterxml.woodstox", module = "woodstox-core")
    }
    api(libs.org.apache.cxf.cxf.rt.features.logging)
    api(libs.org.apache.cxf.cxf.spring.boot.starter.jaxws)
    api(libs.org.apache.cxf.cxf.rt.frontend.jaxws)
    api(libs.eu.ecodex.utils.spring.property.configuration.manager.api)
    api(project(":domibusConnectorControllerLIB"))
    api(project(":domibusConnectorPersistence"))
    api(libs.org.slf4j.slf4j.api)
    // api(libs.javax.jms.javax.jms.api) moved to jakarta
    api(libs.jakarta.jms.api)
    api(libs.eu.domibus.domibus.ws.stubs)
    testImplementation(libs.com.github.database.rider.rider.spring)
    testImplementation(project(":domibusConnectorControllerAPI"))
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.springframework.spring.test)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.activemq)
    testImplementation(libs.org.apache.activemq.activemq.broker)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(project(":domibusConnectorTestData"))
    testImplementation(testFixtures(project(":domibusConnectorPersistence")))
    testImplementation(libs.javax.javaee.api)
}

description = """
    This module is responsible for:
    • Receiving messages from LinkPartners
    • Transforming these messages to the domain model
    • Initial Storage of these messages
    • Handing messages over to the ConnectorController"""

tasks.processTestResources {
    // Disable transformation of any XML test resources but still copy them
    filesMatching("*.properties") {
        expand(project.properties)
    }
}