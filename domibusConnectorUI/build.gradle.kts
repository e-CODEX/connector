plugins {
    id("eu.domibus.connector.java-conventions")
    alias(libs.plugins.lombok)
}

dependencies {
    api(project(":domibusConnectorUILib"))
    api(project(":domibusConnectorStarter"))
    api(project(":domibusConnectorLink"))
    api(project(":domibusConnectorDocumentation"))
    api(libs.eu.ecodex.utils.spring.property.configuration.manager.vaadin.ui)
    api(libs.org.springframework.boot.spring.boot.starter.log4j2)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    api(libs.org.apache.poi.poi)
    api(libs.com.vaadin.vaadin.spring.boot.starter)
    api(libs.com.vaadin.flow.server)
    api(libs.org.vaadin.klaudeta.grid.pagination)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(project(":domibusConnectorTestData"))
    testImplementation(libs.org.assertj.assertj.core)
    compileOnly(libs.com.h2database.h2)
    compileOnly(libs.org.mariadb.jdbc.mariadb.java.client)
    compileOnly(libs.org.postgresql.postgresql)
    compileOnly(libs.com.mysql.mysql.connector.j)
}

description = "domibusConnectorUI"
