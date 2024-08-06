/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
