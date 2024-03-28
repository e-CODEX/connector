package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;


import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class WsActiveLinkPartnerManager {
    private static final Logger LOGGER = LogManager.getLogger(WsActiveLinkPartnerManager.class);

    private final Map<String, WsBackendPluginActiveLinkPartner> certificateDnToLinkPartnerMap = new HashMap<>();

    public synchronized void registerDn(WsBackendPluginActiveLinkPartner wsBackendPluginActiveLinkPartner) {
        String certificateDn = wsBackendPluginActiveLinkPartner.getConfig().getCertificateDn();
        certificateDn = certificateDn.toLowerCase();
        certificateDnToLinkPartnerMap.put(certificateDn, wsBackendPluginActiveLinkPartner);
        LOGGER.info(
                "Registered certificate DN [{}] for BackendClient [{}]",
                certificateDn,
                wsBackendPluginActiveLinkPartner
        );
    }

    public synchronized void deregister(WsBackendPluginActiveLinkPartner wsBackendPluginActiveLinkPartner) {
        String certificateDn = wsBackendPluginActiveLinkPartner.getConfig().getCertificateDn();
        certificateDn = certificateDn.toLowerCase();
        certificateDnToLinkPartnerMap.remove(certificateDn);
        LOGGER.info(
                "Removed certificate DN [{}] for BackendClient [{}]",
                certificateDn,
                wsBackendPluginActiveLinkPartner
        );
    }

    public Optional<WsBackendPluginActiveLinkPartner> getDomibusConnectorLinkPartnerByDn(String certificateDn) {
        certificateDn = certificateDn.toLowerCase();
        WsBackendPluginActiveLinkPartner wsBackendPluginActiveLinkPartner =
                certificateDnToLinkPartnerMap.get(certificateDn);
        return Optional.ofNullable(wsBackendPluginActiveLinkPartner);
    }
}
