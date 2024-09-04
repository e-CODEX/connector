/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.service;

import eu.ecodex.connector.common.DomibusConnectorDefaults;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.exception.LinkPluginException;
import eu.ecodex.connector.persistence.service.DCLinkPersistenceService;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class which reads on startup all configured links from database via
 * {@code LinkInfoPersistenceService} and triggers the {@code DomibusConnectorLinkManager} to create
 * the configured links.
 */
@Configuration
public class DomibusConnectorLinkCreatorConfigurationService {
    private static final Logger LOGGER =
        LogManager.getLogger(DomibusConnectorLinkCreatorConfigurationService.class);
    @Autowired(required = false)
    DCLinkPersistenceService dcLinkPersistenceService;
    @Autowired
    DCActiveLinkManagerService linkManager;
    @Autowired
    DCLinkPluginConfigurationProperties config;

    /**
     * Initializes the DomibusConnectorLinkCreatorConfigurationService. This method is annotated
     * with @PostConstruct and is automatically invoked after the bean has been constructed and all
     * dependencies have been injected. The method checks if autostart is enabled in the
     * configuration. If autostart is enabled, it loads the link configuration either from the
     * database or from the properties environment. If autostart is disabled, it logs a message
     * indicating that no links will be started during connector start.
     */
    @PostConstruct
    public void init() {
        if (config.isAutostart()) {
            if (config.isLoadDbConfig()) {
                if (dcLinkPersistenceService == null) {
                    LOGGER.warn(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Link Autostart Error: DCLinkPersistenceService is not available! "
                            + "Skipping loading link config from DB"
                    );
                } else {
                    LOGGER.info(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Link Autostart is enabled - loading config from DB"
                    );
                    dcLinkPersistenceService.getAllEnabledLinks()
                                            .forEach(this::activateLink);
                }
            }
            if (config.isLoadEnvConfig()) {
                LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Link Autostart is enabled - loading config from Properties Environment"
                );
                loadConfigFromSpringEnvironment();
            }
        } else {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Link Autostart is disabled - no links are going to be started during "
                    + "connector start"
            );
        }
    }

    private void loadConfigFromSpringEnvironment() {
        configureGatewayLinks();

        configureBackendLinks();
    }

    private void configureBackendLinks() {
        List<DCLinkPluginConfigurationProperties.DCLnkPropertyConfig> backends =
            config.getBackend();
        if (backends.isEmpty() && !config.isFailOnLinkPluginError()) {
            LOGGER.warn("No backends are configured!");
        } else if (backends.isEmpty()) {
            var error = String.format(
                "No backends are configured under [%s.backend]"
                    + "\nConnector will not start!", DCLinkPluginConfigurationProperties.PREFIX
            );
            throw new IllegalStateException(error);
        }

        var i = 0;
        var configPrefix = "";
        for (DCLinkPluginConfigurationProperties.DCLnkPropertyConfig backend : backends) {
            configPrefix =
                String.format("%s.backend[%d]", DCLinkPluginConfigurationProperties.PREFIX, i);
            DomibusConnectorLinkConfiguration linkConfig = backend.getLinkConfig();
            if (linkConfig == null) {
                LOGGER.warn(
                    "Backend configuration is incomplete!\nCheck config under [{}[{}].*]!",
                    configPrefix, i
                );
                continue;
            }
            linkConfig.setConfigurationSource(ConfigurationSource.ENV);

            var linkPartnerIndex = 0;
            var linkPartnerConfigPrefix =
                String.format("%s.link-partner[%d]", configPrefix, linkPartnerIndex);
            for (DomibusConnectorLinkPartner linkPartner : backend.getLinkPartners()) {
                linkPartner.setLinkConfiguration(linkConfig);
                try {
                    linkPartner.setLinkType(LinkType.BACKEND);
                    this.activateLink(linkPartner);
                } catch (Exception e) {
                    var error = String.format(
                        "Exception thrown while activating link partner under: [%s.*]",
                        linkPartnerConfigPrefix
                    );
                    LOGGER.warn(error, e);
                    if (config.isFailOnLinkPluginError()) {
                        throw new RuntimeException(error, e);
                    }
                }
                linkPartnerIndex++;
            }
            i++;
        }
    }

    private void configureGatewayLinks() {
        final DCLinkPluginConfigurationProperties.DCLnkPropertyConfig gateway = config.getGateway();
        if (gateway == null && !config.isFailOnLinkPluginError()) {
            LOGGER.warn("No gateway configured!");
            return;
        } else if (gateway == null) {
            var error = String.format(
                "No gateway is configured under [%s.gateway]\n"
                    + "Connector will not start!", DCLinkPluginConfigurationProperties.PREFIX
            );
            throw new IllegalStateException(error);
        }
        DomibusConnectorLinkConfiguration linkConfig = gateway.getLinkConfig();

        if (linkConfig == null) {
            LOGGER.warn("Gateway link config incomplete!\nCheck config under "
                            + DCLinkPluginConfigurationProperties.PREFIX + ".gateway");
            return;
        }
        // set config name to default for default gw
        linkConfig.setConfigName(
            new DomibusConnectorLinkConfiguration.LinkConfigName("gateway-config"));
        linkConfig.setConfigurationSource(ConfigurationSource.ENV);

        int gwLinkCount = gateway.getLinkPartners().size();
        if (gwLinkCount != 1) {
            var error = String.format(
                "There are [%d] configured gateway links - only ONE gateway link is supported!",
                gwLinkCount
            );
            LOGGER.error(error);
            throw new RuntimeException("Illegal Configuration! " + error);
        }

        DomibusConnectorLinkPartner lp = gateway.getLinkPartners().getFirst();

        lp.setLinkType(LinkType.GATEWAY);
        lp.setLinkConfiguration(gateway.getLinkConfig());
        lp.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName(
            DomibusConnectorDefaults.DEFAULT_GATEWAY_NAME));
        LOGGER.info(
            LoggingMarker.Log4jMarker.CONFIG, "Activating gateway link configuration [{}]", lp);
        this.activateLink(lp);
    }

    private void activateLink(DomibusConnectorLinkPartner linkInfo) {
        if (!linkInfo.isEnabled()) {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Enabled flag of link [{}] is false - LinkPartner will not be started!", linkInfo
            );
            return;
        }
        try {
            linkManager.activateLinkPartner(linkInfo);
        } catch (LinkPluginException e) {
            var error = String.format("Exception while activating Link [%s]", linkInfo);
            if (config.isFailOnLinkPluginError()) {
                var msg = String.format(
                    "Failing startup because property [%s.fail-on-link-plugin-error=true]: %s",
                    DCLinkPluginConfigurationProperties.PREFIX, error
                );
                throw new RuntimeException(msg, e);
            } else {
                LOGGER.warn(error, e);
            }
        }
    }
}
