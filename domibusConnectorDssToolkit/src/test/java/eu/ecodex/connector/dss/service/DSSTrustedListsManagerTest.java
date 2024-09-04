/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.dss.service;

import eu.ecodex.connector.common.SpringProfiles;
import eu.ecodex.connector.common.configuration.ConnectorConverterAutoConfiguration;
import eu.ecodex.connector.common.service.DCKeyStoreService;
import eu.ecodex.connector.dss.configuration.BasicDssConfiguration;
import eu.ecodex.utils.spring.converter.ConverterAutoConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {BasicDssConfiguration.class,
        DSSTrustedListsManager.class,
        ConverterAutoConfiguration.class,
        ConnectorConverterAutoConfiguration.class,
        DCKeyStoreService.class
    },
    properties = "connector.dss.tlCacheLocation=file:./target/tlcache/"

)
@ActiveProfiles({"seclib-test", SpringProfiles.TEST, "dss-tl-test"})
class DSSTrustedListsManagerTest {
    @Autowired
    DSSTrustedListsManager dssTrustedListsManager;

    @Test
    void testStartup() {
        Assertions.assertThat(dssTrustedListsManager.getAllSourceNames()).hasSize(2);
        Assertions.assertThat(dssTrustedListsManager.getCertificateSource("list1")).isPresent();
    }
}
