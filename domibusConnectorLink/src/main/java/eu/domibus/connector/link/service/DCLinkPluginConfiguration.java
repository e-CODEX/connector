/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.link.service;

import eu.domibus.connectorplugins.link.gwwspullplugin.DCGatewayPullPlugin;
import eu.domibus.connectorplugins.link.gwwspushplugin.WsGatewayPlugin;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.Validator;

/**
 * The DCLinkPluginConfiguration class is a configuration class that defines the beans for the DC
 * Gateway Pull Plugin, WS Gateway Plugin, and WS Backend Plugin. It also enables configuration
 * properties for the DCLinkPluginConfigurationProperties class and imports the
 * DomibusConnectorLinkCreatorConfigurationService.
 */
@Configuration
@EnableConfigurationProperties(DCLinkPluginConfigurationProperties.class)
@Import(DomibusConnectorLinkCreatorConfigurationService.class)
public class DCLinkPluginConfiguration {
    public static final String LINK_PLUGIN_PROFILE_NAME = "linkplugins";

    @Bean
    @ConditionalOnProperty(
        prefix = DCLinkPluginConfigurationProperties.PREFIX + ".plugin-"
            + DCGatewayPullPlugin.IMPL_NAME,
        value = "enabled", havingValue = "true", matchIfMissing = true
    )
    public DCGatewayPullPlugin dcGatewayPullPlugin() {
        return new DCGatewayPullPlugin();
    }

    @Bean
    @ConditionalOnProperty(
        prefix = DCLinkPluginConfigurationProperties.PREFIX + ".plugin-"
            + WsGatewayPlugin.IMPL_NAME,
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public WsGatewayPlugin wsGatewayPlugin() {
        return new WsGatewayPlugin();
    }

    @Bean
    @ConditionalOnProperty(
        prefix = DCLinkPluginConfigurationProperties.PREFIX + ".plugin-"
            + WsBackendPlugin.IMPL_NAME,
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true
    )
    public WsBackendPlugin wsBackendPlugin(
        ConfigurableApplicationContext applicationContext, Validator validator) {
        return new WsBackendPlugin(applicationContext, validator);
    }
}
