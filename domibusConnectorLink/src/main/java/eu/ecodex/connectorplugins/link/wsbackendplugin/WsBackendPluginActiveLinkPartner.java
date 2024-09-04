/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.wsbackendplugin;

import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginLinkPartnerConfigurationProperties;
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
