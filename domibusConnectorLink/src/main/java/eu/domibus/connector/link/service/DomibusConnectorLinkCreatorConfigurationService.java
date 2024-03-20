package eu.domibus.connector.link.service;

import eu.domibus.connector.common.DomibusConnectorDefaults;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import eu.domibus.connector.persistence.service.DCLinkPersistenceService;
import eu.domibus.connector.tools.logging.LoggingMarker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

import java.util.List;

import static eu.domibus.connector.link.service.DCLinkPluginConfiguration.LINK_PLUGIN_PROFILE_NAME;

/**
 * Spring configuration class which reads
 * on startup all configured links from database via
 * {@code LinkInfoPersistenceService}
 * and triggers the {@code DomibusConnectorLinkManager}
 * to create the configured links
 */
@Configuration
public class DomibusConnectorLinkCreatorConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorLinkCreatorConfigurationService.class);

    @Autowired(required = false)
    DCLinkPersistenceService dcLinkPersistenceService;

    @Autowired
    DCActiveLinkManagerService linkManager;

    @Autowired
    DCLinkPluginConfigurationProperties config;

    @PostConstruct
    public void init() {
        if (config.isAutostart()) {
            if (config.isLoadDbConfig()) {
                if (dcLinkPersistenceService == null) {
                    LOGGER.warn(LoggingMarker.Log4jMarker.CONFIG, "Link Autostart Error: DCLinkPersistenceService is not available! Skipping loading link config from DB");
                } else {
                    LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Link Autostart is enabled - loading config from DB");
                    dcLinkPersistenceService.getAllEnabledLinks().stream().forEach(this::activateLink);
                }
            }
            if (config.isLoadEnvConfig()) {
                LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Link Autostart is enabled - loading config from Properties Environment");
                loadConfigFromSpringEnvironment();
            }
        } else {
            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Link Autostart is disabled - no links are going to be started during connector start");
        }
    }

    private void loadConfigFromSpringEnvironment() {
        configureGatewayLinks();

        configureBackendLinks();

    }

    private void configureBackendLinks() {
        List<DCLinkPluginConfigurationProperties.DCLnkPropertyConfig> backends = config.getBackend();
        if (backends.isEmpty() && !config.isFailOnLinkPluginError()) {
            LOGGER.warn("No backends are configured!");
        } else if (backends.isEmpty()) {
            String error = String.format("No backends are configured under [%s.backend]\nConnector will not start!", DCLinkPluginConfigurationProperties.PREFIX);
            throw new IllegalStateException(error);
        }

        int i = 0;
        String configPrefix = "";
        for (DCLinkPluginConfigurationProperties.DCLnkPropertyConfig backend : backends) {
            configPrefix = String.format("%s.backend[%d]", DCLinkPluginConfigurationProperties.PREFIX, i);
            DomibusConnectorLinkConfiguration linkConfig = backend.getLinkConfig();
            if (linkConfig == null) {
                LOGGER.warn("Backend configuration is incomplete!\nCheck config under [{}[{}].*]!", configPrefix, i);
                continue;
            }
            linkConfig.setConfigurationSource(ConfigurationSource.ENV);

            int linkPartnerIndex = 0;
            String linkPartnerConfigPrefix = String.format("%s.link-partner[%d]", configPrefix, linkPartnerIndex);
            for (DomibusConnectorLinkPartner linkPartner : backend.getLinkPartners()) {
                linkPartner.setLinkConfiguration(linkConfig);
                try {
                    linkPartner.setLinkType(LinkType.BACKEND);
                    this.activateLink(linkPartner);
                } catch (Exception e) {
                    String error = String.format("Exception thrown while activating link partner under: [%s.*]", linkPartnerConfigPrefix);
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
            String error = String.format("No gateway is configured under [%s.gateway]\nConnector will not start!", DCLinkPluginConfigurationProperties.PREFIX);
            throw new IllegalStateException(error);
        }
        DomibusConnectorLinkConfiguration linkConfig = gateway.getLinkConfig();

        if (linkConfig == null) {
            LOGGER.warn("Gateway link config incomplete!\nCheck config under " + DCLinkPluginConfigurationProperties.PREFIX + ".gateway");
            return;
        }
        //set config name to default for default gw
        linkConfig.setConfigName(new DomibusConnectorLinkConfiguration.LinkConfigName("gateway-config"));
        linkConfig.setConfigurationSource(ConfigurationSource.ENV);

        int gwLinkCount = gateway.getLinkPartners().size();
        if (gwLinkCount != 1) {
            String error = String.format("There are [%d] configured gateway links - only ONE gateway link is supported!", gwLinkCount);
            LOGGER.error(error);
            throw new RuntimeException("Illegal Configuration! " + error);
        }

        DomibusConnectorLinkPartner lp = gateway.getLinkPartners().get(0);

        lp.setLinkType(LinkType.GATEWAY);
        lp.setLinkConfiguration(gateway.getLinkConfig());
        lp.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName(DomibusConnectorDefaults.DEFAULT_GATEWAY_NAME));
        LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Activating gateway link configuration [{}]", lp);
        this.activateLink(lp);

    }

    private void activateLink(DomibusConnectorLinkPartner linkInfo) {
        if (!linkInfo.isEnabled()) {
            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Enabled flag of link [{}] is false - LinkPartner will not be started!", linkInfo);
            return;
        }
        try {
            linkManager.activateLinkPartner(linkInfo);
        } catch (LinkPluginException e) {
            String error = String.format("Exception while activating Link [%s]", linkInfo);
            if (config.isFailOnLinkPluginError()) {
                String msg = String.format("Failing startup because property [%s.fail-on-link-plugin-error=true]: %s",
                        DCLinkPluginConfigurationProperties.PREFIX, error);
                throw new RuntimeException(msg, e);
            } else {
                LOGGER.warn(error, e);
            }
        }
    }


}