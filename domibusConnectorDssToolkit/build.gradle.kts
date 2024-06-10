plugins {
    id("eu.domibus.connector.java-conventions")
}

dependencies {
    api(project(":domibusConnectorControllerLIB"))
    api(libs.eu.ecodex.utils.spring.boot.property.converter)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.document)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.xades)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.service)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.utils.apache.commons)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.tsl.validation)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.asic.xades) {
        exclude(group = "org.apache.santuario", module = "xmlsec")
    }
    api(libs.eu.europa.ec.joinup.sd.dss.dss.token)
    api(libs.eu.europa.ec.joinup.sd.dss.dss.pades)
    api(libs.org.apache.santuario.xmlsec)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.platform.launcher)

    testImplementation(libs.org.springframework.boot.spring.boot.test)
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.springframework.spring.test)
}

description = "This module provides services which can be used for certificate verification and signing."
