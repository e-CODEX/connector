/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.common.configuration.ConnectorConfigurationProperties;
import eu.ecodex.connector.persistence.service.DCBusinessDomainPersistenceService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
    classes = {DCBusinessDomainManagerImpl.class, ConnectorConfigurationProperties.class}
)
class DCBusinessDomainManagerImplTest {
    @Autowired
    DCBusinessDomainManagerImpl dcBusinessDomainManager;
    @MockBean
    DCBusinessDomainPersistenceService dcBusinessDomainPersistenceService;

    @Test
    void testUpdateProperties() {
        Map<String, String> currentProperties = new HashMap<>();
        currentProperties.put("prop1", "value1");
        currentProperties.put("prop2", "value2");
        currentProperties.put("prop3", "value3");

        Map<String, String> changedProperties = new HashMap<>();
        changedProperties.put("prop1", null); // should be removed
        changedProperties.put("prop2", "value_changed"); // should be changed

        Map<String, String> stringStringMap =
            dcBusinessDomainManager.updateChangedProperties(currentProperties, changedProperties);

        Map<String, String> expectedResultMap = new HashMap<>();
        expectedResultMap.put("prop2", "value_changed");
        expectedResultMap.put("prop3", "value3");

        assertThat(stringStringMap).containsExactlyEntriesOf(expectedResultMap);
    }
}
