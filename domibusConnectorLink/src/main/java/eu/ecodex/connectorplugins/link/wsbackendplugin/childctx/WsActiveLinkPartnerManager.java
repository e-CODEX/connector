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

import eu.ecodex.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * The WsActiveLinkPartnerManager class is responsible for managing active link partners for the WS
 * backend plugin in the Domibus Connector.
 */
@Component
public class WsActiveLinkPartnerManager {
    private static final Logger LOGGER = LogManager.getLogger(WsActiveLinkPartnerManager.class);
    private final Map<String, WsBackendPluginActiveLinkPartner> certificateDnToLinkPartnerMap =
        new HashMap<>();

    /**
     * Registers a certificate DN for a backend client in the active link partner manager.
     *
     * @param wsBackendPluginActiveLinkPartner The backend client object to register.
     */
    public synchronized void registerDn(
        WsBackendPluginActiveLinkPartner wsBackendPluginActiveLinkPartner) {
        String certificateDn = wsBackendPluginActiveLinkPartner.getConfig().getCertificateDn();
        certificateDn = certificateDn.toLowerCase();
        certificateDnToLinkPartnerMap.put(certificateDn, wsBackendPluginActiveLinkPartner);
        LOGGER.info(
            "Registered certificate DN [{}] for BackendClient [{}]", certificateDn,
            wsBackendPluginActiveLinkPartner
        );
    }

    /**
     * Removes the registration of a backend client from the active link partner manager.
     *
     * @param wsBackendPluginActiveLinkPartner The backend client object to deregister.
     */
    public synchronized void deregister(
        WsBackendPluginActiveLinkPartner wsBackendPluginActiveLinkPartner) {
        String certificateDn = wsBackendPluginActiveLinkPartner.getConfig().getCertificateDn();
        certificateDn = certificateDn.toLowerCase();
        certificateDnToLinkPartnerMap.remove(certificateDn);
        LOGGER.info(
            "Removed certificate DN [{}] for BackendClient [{}]", certificateDn,
            wsBackendPluginActiveLinkPartner
        );
    }

    /**
     * Retrieves the active link partner in the Domibus Connector by the given certificate DN.
     *
     * @param certificateDn The certificate DN to search for in the active link partner manager.
     * @return An Optional containing the WsBackendPluginActiveLinkPartner object if found, or an
     *      empty Optional if not found.
     */
    public Optional<WsBackendPluginActiveLinkPartner> getDomibusConnectorLinkPartnerByDn(
        String certificateDn) {
        certificateDn = certificateDn.toLowerCase();
        var wsBackendPluginActiveLinkPartner = certificateDnToLinkPartnerMap.get(certificateDn);
        return Optional.ofNullable(wsBackendPluginActiveLinkPartner);
    }
}
