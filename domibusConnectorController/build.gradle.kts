plugins {
    id("eu.domibus.connector.java-conventions")
    alias(libs.plugins.lombok)
}

dependencies {
    api(project(":domibusConnectorControllerAPI"))
    api(project(":domibusConnectorSecurityToolkit"))
    api(project(":domibusConnectorEvidencesToolkit"))
    api(project(":domibusConnectorPersistence"))
    api(libs.eu.ecodex.utils.spring.quartz.scheduled)
    api(libs.org.springframework.boot.spring.boot.starter.jta.atomikos)
    api(libs.org.springframework.boot.spring.boot.starter.artemis)
    api(libs.org.springframework.boot.spring.boot.starter.quartz)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api(libs.org.testcontainers.testcontainers)
    api(libs.org.apache.activemq.artemis.jms.server)
    api(libs.com.google.code.findbugs.annotations)
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.apache.logging.log4j.log4j.api)
    api(libs.org.apache.logging.log4j.log4j.slf4j.impl)
    api(libs.org.apache.logging.log4j.log4j.core)
    api(libs.org.slf4j.jcl.over.slf4j)
    api(libs.org.slf4j.jul.to.slf4j)
//    testImplementation(project(":domibusConnectorPersistence")){
//        artifact {  classifier = "tests" }
//    }
    testImplementation(testFixtures(project(":domibusConnectorPersistence")))
    testImplementation(project(":domibusConnectorTestData"))
    testImplementation(libs.com.h2database.h2)
    testImplementation(libs.org.mockito.mockito.core)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.liquibase.liquibase.core)
    testImplementation(testFixtures(project(":domibusConnectorControllerAPI")))
//    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
//    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)

    testImplementation(libs.org.dbunit.dbunit)
    testImplementation(libs.com.github.database.rider.rider.spring)
    compileOnly(libs.com.oracle.database.jdbc.ojdbc8)
    compileOnly(libs.org.mariadb.jdbc.mariadb.java.client)
    compileOnly(libs.org.postgresql.postgresql)
    compileOnly(libs.com.mysql.mysql.connector.j)
}

description = "Does the business work with the messages: Generates the evidences, resolves and verifies the ASIC-S container, creates the TRUST-Tokens. It hands over the messages to the GWLink module for GW-Communication and to the BackendLinkModule for communication with the connectorClients. For this purpose it uses other modules like persistence, evidenceToolkit, securityToolkit,..."

tasks.processTestResources {
    from("src/test/resources")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // Add filtered test resources
    val filteredTestResources = project.fileTree("src/test/resources-filtered")
    filteredTestResources.filter { it.isFile }.forEach { file ->
        from(file) {
            // Enable resource filtering for filtered test resources
            filter(org.apache.tools.ant.filters.ReplaceTokens::class.java)
        }
    }
}