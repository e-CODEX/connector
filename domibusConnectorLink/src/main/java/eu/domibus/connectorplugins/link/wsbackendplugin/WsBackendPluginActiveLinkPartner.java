package eu.domibus.connectorplugins.link.wsbackendplugin;

import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginLinkPartnerConfigurationProperties;


public class WsBackendPluginActiveLinkPartner extends ActiveLinkPartner {
    WsBackendPluginLinkPartnerConfigurationProperties config;

    public WsBackendPluginLinkPartnerConfigurationProperties getConfig() {
        return config;
    }

    public void setConfig(WsBackendPluginLinkPartnerConfigurationProperties config) {
        this.config = config;
    }
}
