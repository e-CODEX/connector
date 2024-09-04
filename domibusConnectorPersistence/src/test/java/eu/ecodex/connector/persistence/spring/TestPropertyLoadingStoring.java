/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.spring;

import static eu.ecodex.connector.persistence.spring.PersistenceProfiles.STORAGE_DB_PROFILE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.common.service.ConfigurationPropertyLoaderServiceImpl;
import eu.ecodex.connector.common.service.CurrentBusinessDomain;
import eu.ecodex.connector.common.service.DCBusinessDomainManager;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test", "db_h2", "storage-db", STORAGE_DB_PROFILE_NAME, "prop-test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPropertyLoadingStoring {
    @SpringBootApplication(scanBasePackages = "eu.ecodex.connector")
    public static class TestContext {
    }

    @Autowired
    ConfigurationPropertyLoaderServiceImpl configurationPropertyLoaderService;
    @Autowired
    DCBusinessDomainManager dcBusinessDomainManager;
    @Autowired
    TestProperties testProperties;

    @Test
    @Order(1)
    void testPropLoad() {
        try {
            DomibusConnectorBusinessDomain.BusinessDomainId d1 =
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
            CurrentBusinessDomain.setCurrentBusinessDomain(d1);

            TestProperties changed1 =
                configurationPropertyLoaderService.loadConfiguration(d1, TestProperties.class);
            changed1.setT1("abc1");
            changed1.setCamelCaseProperty("camelCase");

            assertThat(testProperties.getT1()).isEqualTo("test1");

            configurationPropertyLoaderService.updateConfiguration(d1, changed1);

            assertThat(testProperties.getT1()).isEqualTo("abc1");
            assertThat(testProperties.getCamelCaseProperty()).isEqualTo("camelCase");
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    @Test
    @Order(3)
    void testPropLoadDifferentBusinessDomain() {
        DomibusConnectorBusinessDomain b = new DomibusConnectorBusinessDomain();
        DomibusConnectorBusinessDomain.BusinessDomainId bid =
            new DomibusConnectorBusinessDomain.BusinessDomainId("b2");
        b.setId(bid);
        dcBusinessDomainManager.createBusinessDomain(b);

        try {
            DomibusConnectorBusinessDomain.BusinessDomainId defaultDomain =
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId();

            // update properties in default domain
            TestProperties changed1 =
                configurationPropertyLoaderService.loadConfiguration(defaultDomain,
                                                                     TestProperties.class
                );
            changed1.setT1("defaultDomain");
            changed1.setCamelCaseProperty("defaultDomain");
            configurationPropertyLoaderService.updateConfiguration(defaultDomain, changed1);

            DomibusConnectorBusinessDomain.BusinessDomainId testDomain2 = bid;
            // update properties in testdomain
            TestProperties changed2 =
                configurationPropertyLoaderService.loadConfiguration(testDomain2,
                                                                     TestProperties.class
                );
            changed2.setT1("testDomain2");
            changed2.setCamelCaseProperty("testDomain2");
            configurationPropertyLoaderService.updateConfiguration(testDomain2, changed2);

            // verify properties in test domain
            CurrentBusinessDomain.setCurrentBusinessDomain(testDomain2);
            assertThat(testProperties.getT1()).isEqualTo("testDomain2");
            assertThat(testProperties.getCamelCaseProperty()).isEqualTo("testDomain2");

            // verify properties in default domain
            CurrentBusinessDomain.setCurrentBusinessDomain(defaultDomain);
            assertThat(testProperties.getT1()).isEqualTo("defaultDomain");
            assertThat(testProperties.getCamelCaseProperty()).isEqualTo("defaultDomain");
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }
}
