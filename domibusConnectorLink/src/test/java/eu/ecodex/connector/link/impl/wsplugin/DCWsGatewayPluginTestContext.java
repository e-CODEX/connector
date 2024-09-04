/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.impl.wsplugin;

import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.persistence.dao.DomibusConnectorLinkConfigurationDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorLinkPartnerDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import eu.ecodex.connector.persistence.model.PDomibusConnectorLinkPartner;
import eu.ecodex.connector.persistence.service.DCLinkPersistenceService;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * The DCWsGatewayPluginTestContext class represents the test context for the DCWsGatewayPlugin.
 * It provides mock instances of the necessary DAOs and link configurations for testing.
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@Import({LinkTestContext.class, CxfAutoConfiguration.class, DCWsGatewayPlugin.class})
@Profile(DCWsGatewayPluginTestContext.WS_TEST_PROFILE)
public class DCWsGatewayPluginTestContext {
    public static final String WS_TEST_PROFILE = "ws-test";
    public static final String WS_GATEWAY_TEST_PROFILE = "ws-gw-test";
    @Autowired
    DCLinkPersistenceService linkPersistenceService;

    /**
     * Creates a mock instance of the DomibusConnectorLinkPartnerDao class and configures it to
     * return the web service gateway link information when the findAllByEnabledIsTrue method is
     * called, and return the web service gateway link information when the findOneByLinkName method
     * is called with the argument "cn=gw".
     *
     * @return The DomibusConnectorLinkPartnerDao instance with the configured behavior.
     */
    @Bean
    @Primary
    @Profile(WS_TEST_PROFILE)
    public DomibusConnectorLinkPartnerDao domibusConnectorWsLinkPartnerDao() {
        DomibusConnectorLinkPartnerDao dao = Mockito.mock(DomibusConnectorLinkPartnerDao.class);
        Mockito.when(dao.findAllByEnabledIsTrue())
               .thenReturn(Stream.of(getWsGwLinkInfo()).toList());
        Mockito.when(dao.findOneByLinkName("cn=gw"))
               .thenReturn(Optional.of(getWsGwLinkInfo()));

        return dao;
    }

    /**
     * Creates a mock instance of the DomibusConnectorLinkConfigurationDao class and configures it
     * to return the web service gateway link configuration when the method getOneByConfigName is
     * called with the argument "wsgateway".
     *
     * @return The DomibusConnectorLinkConfigurationDao instance with the configured behavior.
     */
    @Bean
    @Primary
    @Profile(WS_TEST_PROFILE)
    public DomibusConnectorLinkConfigurationDao domibusConnectorWsLinkConfigDao() {
        DomibusConnectorLinkConfigurationDao dao =
            Mockito.mock(DomibusConnectorLinkConfigurationDao.class);
        Mockito.when(dao.getOneByConfigName("wsgateway"))
               .thenReturn(Optional.of(getWsGatewayLinkConfig()));

        return dao;
    }

    @Bean
    @Profile(WS_TEST_PROFILE)
    public DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration() {
        return linkPersistenceService.getLinkConfiguration(
            new DomibusConnectorLinkConfiguration.LinkConfigName("wsgateway")).get();
    }

    /**
     * Retrieves the web service gateway link configuration.
     *
     * @return The web service gateway link configuration represented as a
     *      {@link PDomibusConnectorLinkConfiguration} object.
     */
    public static PDomibusConnectorLinkConfiguration getWsGatewayLinkConfig() {
        PDomibusConnectorLinkConfiguration linkConfig = new PDomibusConnectorLinkConfiguration();
        linkConfig.setConfigName("wsgateway");

        HashMap<String, String> props = new HashMap<>();

        props.put(
            "link.wsgatewayplugin.soap.key-store.path",
            "classpath:/keystores/connector-gwlink-keystore.jks"
        );
        props.put("link.wsgatewayplugin.soap.key-store.password", "12345");
        props.put("link.wsgatewayplugin.soap.private-key.alias", "connector");
        props.put("link.wsgatewayplugin.soap.private-key.password", "12345");

        props.put(
            "link.wsgatewayplugin.soap.trust-store.path",
            "classpath:/keystores/connector-gwlink-truststore.jks"
        );
        props.put("link.wsgatewayplugin.soap.trust-store.password", "12345");

        linkConfig.setProperties(props);

        return linkConfig;
    }

    /**
     * Retrieves the web service gateway link information.
     *
     * @return The web service gateway link information represented as a
     *      {@link PDomibusConnectorLinkPartner} object.
     */
    public static PDomibusConnectorLinkPartner getWsGwLinkInfo() {
        PDomibusConnectorLinkPartner linkPartner1 = new PDomibusConnectorLinkPartner();
        String linkName1 = "cn=gw";

        linkPartner1.setLinkName(linkName1);
        linkPartner1.setLinkType(LinkType.GATEWAY);

        linkPartner1.setLinkConfiguration(getWsGatewayLinkConfig());
        linkPartner1.setEnabled(true);

        HashMap<String, String> props = new HashMap<>();
        props.put("name.push-address", "push");
        //        props.put("link.wsgate")

        linkPartner1.setProperties(props);

        return linkPartner1;
    }
}
