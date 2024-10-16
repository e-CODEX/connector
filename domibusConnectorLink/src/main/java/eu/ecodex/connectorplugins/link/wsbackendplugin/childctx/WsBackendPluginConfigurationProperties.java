/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.wsbackendplugin.childctx;

import eu.ecodex.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

/**
 * The WsBackendPluginConfigurationProperties class represents the configuration properties for the
 * WS backend plugin.
 */
@ConfigurationProperties()
@Validated
@Data
public class WsBackendPluginConfigurationProperties {
    /**
     * Specifies the address where the Backend WebService should be published the path specified
     * here is added to the path of the CXF-Servlet (which is per default configured as /service -
     * this leads to the default URL of "/services/backend".
     */
    @ConfigurationLabel("Where should the cxf endpoint for the backends be exposed")
    @ConfigurationDescription(
        """
            Specifies the address where the Backend WebService should be published
            "the path specified here is added to the path of the CXF-Servlet
            "(which is per default configured as /services - this leads to the default URL of
            '/services/backend'"""
    )
    private String backendPublishAddress = "/backend";
    /**
     * SSL Key Store configuration.
     *
     * <p>The SSL-Key Store holds the path to the keyStore and the keyStore password to access the
     * private-key which is needed to establish the TLS connection to the Gateway. The private key
     * is used to authenticate against the Gateway.
     */
    @NestedConfigurationProperty
    @ConfigurationDescription("TLS between backend - Connector")
    private KeyAndKeyStoreAndTrustStoreConfigurationProperties tls;
    @Valid
    @NestedConfigurationProperty
    @NotNull
    @ConfigurationDescription("CXF encryption, signing, certs connector ")
    private KeyAndKeyStoreAndTrustStoreConfigurationProperties soap;
    @Valid
    @NotNull
    @ConfigurationLabel("WS Policy for Backend <-> Connector")
    @ConfigurationDescription(
        "This Property is used to define the location of the ws policy which is used for "
            + "communication with the gateway"
    )
    private Resource wsPolicy = new ClassPathResource("/wsdl/backend.policy.xml");
    private boolean cxfLoggingEnabled = false;
}
