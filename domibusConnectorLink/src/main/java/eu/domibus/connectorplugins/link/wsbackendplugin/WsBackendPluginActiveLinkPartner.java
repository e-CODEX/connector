/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connectorplugins.link.wsbackendplugin;

import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginLinkPartnerConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * The WsBackendPluginActiveLinkPartner class represents an active link partner for the WS backend
 * plugin in the Domibus Connector. It extends the ActiveLinkPartner class and contains additional
 * configuration properties specific to the WS backend plugin.
 */
@Getter
@Setter
public class WsBackendPluginActiveLinkPartner extends ActiveLinkPartner {
    WsBackendPluginLinkPartnerConfigurationProperties config;
}
