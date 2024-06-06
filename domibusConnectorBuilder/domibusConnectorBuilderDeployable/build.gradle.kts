plugins {
    id("eu.domibus.connector.java-conventions")
    war
}

dependencies {
    api(project(":domibusConnectorUI"))
    api(project(":domibusConnectorStarter"))
    runtimeOnly(libs.org.fusesource.jansi.jansi.x1)
    providedCompile(libs.com.h2database.h2)
    providedCompile(libs.org.mariadb.jdbc.mariadb.java.client)
    providedCompile(libs.org.postgresql.postgresql)
    providedCompile(libs.com.mysql.mysql.connector.j)
}

description = "domibusConnectorBuilderDeployable"

tasks.named<Jar>("jar") {
    isEnabled = true
}
