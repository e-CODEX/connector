/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the WsBackendPluginLinkPartnerConfigurationProperties class.
 * This class represents the configuration properties required for a link partner in the
 * WS backend plugin.
 */
@ConfigurationProperties()
@Validated
@Data
public class WsBackendPluginLinkPartnerConfigurationProperties {
    @ConfigurationLabel("The address where the link partner is available")
    @ConfigurationDescription(
        "Configure here the address where the remote soap service is listening"
    )
    private String pushAddress = "";
    @ConfigurationLabel("Encryption Alias")
    @ConfigurationDescription(
        "The alias of the certificate of the link partner. So the connector can find \n"
            + "the correct certificate and us this public key to encrypt the message"
    )
    private String encryptionAlias = "";
    private String certificateDn = "";
}
