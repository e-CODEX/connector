plugins {
    id("eu.domibus.connector.java-conventions")
}

dependencies {
    api(project(":domibusConnectorUI"))
    api(project(":domibusConnectorStarter"))
    api(libs.com.h2database.h2)
    api(libs.org.mariadb.jdbc.mariadb.java.client)
    api(libs.org.liquibase.liquibase.core)
    api(libs.com.mysql.mysql.connector.j)
    api(libs.org.fusesource.jansi.jansi)
    compileOnly(libs.org.postgresql.postgresql)
    compileOnly(libs.org.springframework.boot.spring.boot.loader)
    compileOnly(libs.com.oracle.database.jdbc.ojdbc8)
}

description = "domibusConnectorBuilderStandalone"
