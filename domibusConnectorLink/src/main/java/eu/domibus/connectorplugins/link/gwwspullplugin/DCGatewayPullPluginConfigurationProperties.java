/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.gwwspullplugin;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

/**
 * The DCGatewayPullPluginConfigurationProperties class represents the configuration properties for
 * the Domibus Connector Pull Gateway Plugin.
 */
@ConfigurationProperties()
@Validated
@Getter
@Setter
public class DCGatewayPullPluginConfigurationProperties {
    @NotNull
    @NotBlank
    @ConfigurationLabel("Pull Gateway Service URL")
    @ConfigurationDescription(
        "The URL of the domibus connector pull gateway plugin webservice.\nEg.: "
            + "<domibus url>/services/pull-gw"
    )
    private String gwAddress;
    /**
     * SSL Key Store configuration.
     *
     * <p>The SSL-Key Store holds the path to the keyStore and the keyStore password to access the
     * private-key which is needed to establish the TLS connection to the Gateway. The private key
     * is used to authenticate against the Gateway.
     */
    @NestedConfigurationProperty
    @ConfigurationDescription("TLS between GW - Connector")
    private KeyAndKeyStoreAndTrustStoreConfigurationProperties tls;
    @Valid
    @NestedConfigurationProperty
    @NotNull
    @ConfigurationDescription("CXF encryption, signing, certs connector - GW")
    private CxfTrustKeyStoreConfigurationProperties soap;
    @Valid
    @NotNull
    @ConfigurationLabel("WS Policy for GW <-> Connector Link")
    @ConfigurationDescription(
        "This Property is used to define the location of the ws policy which is used for "
            + "communication with the gateway. \nDefault: /wsdl/backend.policy.xml"
    )
    private Resource wsPolicy = new ClassPathResource("/wsdl/backend.policy.xml");
    @ConfigurationLabel("WS Policy for GW <-> Connector Link")
    @ConfigurationDescription(
        "This Property is used to activate CXF logging interceptor, which will log the CXF "
            + "requests. \nDefault: false"
    )
    private boolean cxfLoggingEnabled = false;
}
