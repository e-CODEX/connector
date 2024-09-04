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
import static org.mockito.ArgumentMatchers.eq;

import eu.ecodex.connector.common.configuration.ConnectorConfigurationProperties;
import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.utils.service.MyTestProperties;
import eu.ecodex.connector.utils.service.MyTestProperties2;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootTest(
    properties = {
        "connector.confirmation-messages.retrieval.service.service-type=serviceType",
        "connector.confirmation-messages.retrieval.service.name=aService",
        "connector.confirmation-messages.retrieval.action=retrievalAction",
        "test.example2.prop1=abc",
        "test.example2.prop2=123",
        "test.example2.list[0]=abc",
        "test.example2.list[1]=def"
    },
    classes = ConfigurationPropertyLoaderServiceImplTest.TestContext.class
)
class ConfigurationPropertyLoaderServiceImplTest {
    private static final Logger LOGGER =
        LogManager.getLogger(ConfigurationPropertyLoaderServiceImplTest.class);

    @EnableConfigurationProperties({ConnectorConfigurationProperties.class})
    @SpringBootApplication(
        scanBasePackages = {"eu.ecodex.connector.utils", "eu.ecodex.connector.common"}
    )
    public static class TestContext {
        @Bean
        public ApplicationListener<BusinessDomainConfigurationChange> eventListener() {
            return e -> lastChange = e;
        }
    }

    @Autowired
    ConfigurationPropertyLoaderServiceImpl propertyLoaderService;
    @Autowired
    ApplicationContext ctx;
    @Autowired
    MyTestProperties2 myTestProperties2;
    @MockBean
    DCBusinessDomainManagerImpl dcBusinessDomainManagerImpl;
    private static BusinessDomainConfigurationChange lastChange;

    @BeforeEach
    public void beforeEach() {
        Mockito.when(dcBusinessDomainManagerImpl.getBusinessDomain(
                   eq(DomibusConnectorBusinessDomain.getDefaultMessageLaneId())))
               .thenReturn(Optional.of(DomibusConnectorBusinessDomain.getDefaultMessageLane()));
    }

    @Test
    void loadConfiguration() {
        EvidenceActionServiceConfigurationProperties evidenceActionServiceConfigurationProperties =
            propertyLoaderService.loadConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                EvidenceActionServiceConfigurationProperties.class
            );

        assertThat(evidenceActionServiceConfigurationProperties).isNotNull();

        EvidenceActionServiceConfigurationProperties.AS4Action action =
            evidenceActionServiceConfigurationProperties.getRetrieval().getAction();
        assertThat(action.getAction()).isEqualTo("retrievalAction");

        EvidenceActionServiceConfigurationProperties.AS4Service service =
            evidenceActionServiceConfigurationProperties.getRetrieval().getService();
        assertThat(service).isNotNull();
        assertThat(service.getName()).as("service is aService").isEqualTo("aService");
        assertThat(service.getConnectorService().getService()).as("Connector Service must be")
                                                              .isEqualTo("aService");
        assertThat(service.getConnectorService().getServiceType()).as(
            "Connector serviceType must be").isEqualTo("serviceType");
    }

    @SuppressWarnings("squid:S1135")
    @Test
    // TODO: repair Test!
    @Disabled("must be repaired")
    void testGetPropertyMap() {
        MyTestProperties myTestProperties = new MyTestProperties();
        myTestProperties.setProp1("prop1");
        myTestProperties.setProp2(23);
        myTestProperties.getNested().setAbc("abc");
        myTestProperties.getNested().setDuration(Duration.ofDays(23));
        myTestProperties.getNested().setaVeryLongPropertyName("propLong");

        MyTestProperties.NestedProp n1 = new MyTestProperties.NestedProp();
        n1.setAbc("abc");
        n1.setaVeryLongPropertyName("verylongprop");

        myTestProperties.getNestedPropList().add(n1);

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(
            "test.example.nested-prop-list[0].a-very-long-property-name", "verylongprop");
        expectedMap.put("test.example.nested-prop-list[0].abc", "abc");
        expectedMap.put("test.example.prop1", "prop1");
        expectedMap.put("test.example.prop2", "23");
        expectedMap.put("test.example.nested.abc", "abc");
        expectedMap.put("test.example.nested.duration", "PT552H");
        expectedMap.put("test.example.nested.a-very-long-property-name", "propLong");

        Map<String, String> propertyMap =
            new HashMap<>(propertyLoaderService.createPropertyMap(myTestProperties));
        assertThat(propertyMap).containsExactlyInAnyOrderEntriesOf(expectedMap);

        LOGGER.info("Mapped properties are: [{}]", propertyMap);
    }
}
