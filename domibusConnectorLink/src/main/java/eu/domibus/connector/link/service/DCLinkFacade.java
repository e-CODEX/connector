/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.service;

import com.google.common.base.CaseFormat;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import eu.domibus.connector.persistence.service.DCLinkPersistenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * Facade for managing connector links in Domibus.
 */
@Service
@ConditionalOnBean(DCLinkPluginConfiguration.class)
public class DCLinkFacade {
    private final DCActiveLinkManagerService linkManager;
    private final DCLinkPersistenceService dcLinkPersistenceService;
    private final DCLinkPluginConfigurationProperties lnkConfig;

    /**
     * The DCLinkFacade class represents a facade for handling DC link operations.
     *
     * @param linkManager              The active link manager service used for managing the DC
     *                                 links.
     * @param dcLinkPersistenceService The DC link persistence service used for accessing and
     *                                 manipulating DC link data.
     * @param props                    The configuration properties for the DC link plugin.
     */
    public DCLinkFacade(
        DCActiveLinkManagerService linkManager,
        DCLinkPersistenceService dcLinkPersistenceService,
        DCLinkPluginConfigurationProperties props) {
        this.linkManager = linkManager;
        this.dcLinkPersistenceService = dcLinkPersistenceService;
        this.lnkConfig = props;
    }

    /**
     * Checks if a given DomibusConnectorLinkPartner is active.
     *
     * @param linkPartner The DomibusConnectorLinkPartner to check.
     * @return True if the link partner is active, false otherwise.
     * @throws IllegalArgumentException if the link partner name is null.
     */
    public boolean isActive(DomibusConnectorLinkPartner linkPartner) {
        if (linkPartner.getLinkPartnerName() == null) {
            throw new IllegalArgumentException("LinkPartner name is not allowed to be null!");
        }
        return linkManager.getActiveLinkPartnerByName(linkPartner.getLinkPartnerName()).isPresent();
    }

    /**
     * Shuts down the active link partner with the given link partner.
     *
     * @param linkPartner the link partner to be shut down
     * @throws LinkPluginException if no active link partner with the given name is found
     */
    public void shutdownLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        linkManager.shutdownLinkPartner(linkPartner.getLinkPartnerName());
    }

    /**
     * Retrieves a list of DomibusConnectorLinkPartner objects of a specific LinkType.
     *
     * @param linkType The type of link to filter the results by. If null, all links will be
     *                 returned.
     * @return A List of DomibusConnectorLinkPartner objects that match the specified LinkType.
     */
    public List<DomibusConnectorLinkPartner> getAllLinksOfType(LinkType linkType) {
        return getAllLinks()
            .stream()
            .filter(
                l -> Objects.equals(l.getLinkType(), linkType) || linkType == null
            )
            .toList();
    }

    /**
     * Retrieves all the DomibusConnectorLinkPartner objects.
     *
     * @return A List of DomibusConnectorLinkPartner objects.
     */
    public List<DomibusConnectorLinkPartner> getAllLinks() {
        List<DomibusConnectorLinkPartner> allLinks = new ArrayList<>();

        allLinks.addAll(dcLinkPersistenceService.getAllLinks());
        allLinks.addAll(lnkConfig.getBackend().stream()
                                 .filter(Objects::nonNull)
                                 .flatMap(b -> mapCnfg(b, LinkType.BACKEND))
                                 .toList());
        allLinks.addAll(
            mapCnfg(lnkConfig.getGateway(), LinkType.GATEWAY).toList());
        return allLinks;
    }

    private Stream<DomibusConnectorLinkPartner> mapCnfg(
        DCLinkPluginConfigurationProperties.DCLnkPropertyConfig b, LinkType linkType) {
        if (b == null) {
            return Stream.empty();
        }
        var linkConfig = new DomibusConnectorLinkConfiguration();
        if (b.getLinkConfig() != null) {
            BeanUtils.copyProperties(b.getLinkConfig(), linkConfig);
            linkConfig.setProperties(mapKebabCaseToCamelCase(b.getLinkConfig().getProperties()));
        }
        linkConfig.setConfigurationSource(ConfigurationSource.ENV);
        return b.getLinkPartners().stream().map(p -> {
            var linkPartner = new DomibusConnectorLinkPartner();
            BeanUtils.copyProperties(p, linkPartner);
            linkPartner.setConfigurationSource(ConfigurationSource.ENV);
            linkPartner.setLinkConfiguration(linkConfig);
            linkPartner.setLinkType(linkType);
            linkPartner.setProperties(mapKebabCaseToCamelCase(p.getProperties()));
            return linkPartner;
        });
    }

    // convert property names from KebabCase to CamelCase
    // eg.:  cn-name to cnName
    private Map<String, String> mapKebabCaseToCamelCase(Map<String, String> properties) {
        return properties
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                e -> CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, e.getKey()),
                Map.Entry::getValue
            ));
    }

    public Optional<DomibusConnectorLinkPartner> loadLinkPartner(
        DomibusConnectorLinkPartner.LinkPartnerName name) {
        return getAllLinks().stream().filter(l -> name.equals(l.getLinkPartnerName())).findAny();
    }

    /**
     * Activates a link partner in the Domibus Connector.
     *
     * @param linkPartner the DomibusConnectorLinkPartner representing the link partner
     * @throws LinkPluginException if the link partner could not be activated
     */
    public void startLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        Optional<ActiveLinkPartner> activeLinkPartner =
            this.linkManager.activateLinkPartner(linkPartner);
        if (!activeLinkPartner.isPresent()) {
            throw new LinkPluginException("Start failed!");
        }
    }

    /**
     * Deletes a link partner from the Domibus Connector.
     *
     * @param linkPartner The DomibusConnectorLinkPartner to be deleted.
     */
    public void deleteLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        try {
            linkManager.shutdownLinkPartner(linkPartner.getLinkPartnerName());
        } catch (LinkPluginException exception) {
            // handle
        }
        if (ConfigurationSource.DB == linkPartner.getConfigurationSource()) {
            dcLinkPersistenceService.deleteLinkPartner(linkPartner);
        }
    }

    /**
     * Updates the link partner information.
     *
     * @param linkPartner The DomibusConnectorLinkPartner object containing the updated
     *                    information.
     */
    public void updateLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        if (linkPartner.getConfigurationSource() == ConfigurationSource.DB) {
            dcLinkPersistenceService.updateLinkPartner(linkPartner);
        }
    }

    /**
     * Retrieves a DomibusConnectorLinkConfiguration based on the provided config name.
     *
     * @param configName The name of the link configuration.
     * @return An Optional containing the DomibusConnectorLinkConfiguration if found, or empty if
     *      not found.
     */
    public Optional<DomibusConnectorLinkConfiguration> loadLinkConfig(
        DomibusConnectorLinkConfiguration.LinkConfigName configName) {
        List<DomibusConnectorLinkConfiguration> allConfigs = getAllConfigurations();
        return allConfigs.stream()
                         .filter(c -> configName.equals(c.getConfigName()))
                         .findAny();
    }

    private List<DomibusConnectorLinkConfiguration> getAllConfigurations() {
        List<DomibusConnectorLinkConfiguration> allLinkConfigurations =
            dcLinkPersistenceService.getAllLinkConfigurations();

        return Stream.of(
                         getAllLinks().stream()
                                      .map(DomibusConnectorLinkPartner::getLinkConfiguration),
                         allLinkConfigurations.stream()
                     )
                     .flatMap(Function.identity())
                     .distinct()
                     .toList();
    }

    /**
     * Updates the link configuration.
     *
     * @param linkConfig The DomibusConnectorLinkConfiguration object containing the updated
     *                   configuration.
     */
    public void updateLinkConfig(DomibusConnectorLinkConfiguration linkConfig) {
        if (linkConfig.getConfigurationSource() == ConfigurationSource.DB) {
            dcLinkPersistenceService.updateLinkConfig(linkConfig);
        }
    }

    /**
     * Retrieves a list of all DomibusConnectorLinkConfigurations that support a specified
     * LinkType.
     *
     * @param linkType The type of link to filter the results by.
     * @return A List of DomibusConnectorLinkConfiguration objects that support the specified
     *      LinkType.
     */
    public List<DomibusConnectorLinkConfiguration> getAllLinkConfigurations(LinkType linkType) {
        // get all configurations which support the linkType
        Stream<DomibusConnectorLinkConfiguration> stream1 =
            dcLinkPersistenceService.getAllLinkConfigurations().stream()
                                    .filter(c -> getLinkPluginByName(c.getLinkImpl())
                                        .map(p -> p.getFeatures()
                                                   .contains(
                                                       getPluginFeatureFromLinkType(linkType)))
                                        .orElse(false));
        // get all configurations which have a link partner with this link type
        Stream<DomibusConnectorLinkConfiguration> stream2 = getAllLinksOfType(linkType)
            .stream()
            .map(DomibusConnectorLinkPartner::getLinkConfiguration);
        // merge together
        return Stream.of(stream1, stream2)
                     .flatMap(Function.identity())
                     .distinct()
                     .toList();
    }

    private PluginFeature getPluginFeatureFromLinkType(LinkType linkType) {
        PluginFeature pf = null;
        if (linkType == LinkType.GATEWAY) {
            pf = PluginFeature.GATEWAY_PLUGIN;
        } else if (linkType == LinkType.BACKEND) {
            pf = PluginFeature.BACKEND_PLUGIN;
        }
        return pf;
    }

    /**
     * Retrieves a list of available LinkPlugins that support the specified LinkType.
     *
     * @param linkType The LinkType to filter the results by.
     * @return A List of LinkPlugin objects that support the specified LinkType.
     */
    public List<LinkPlugin> getAvailableLinkPlugins(LinkType linkType) {
        return linkManager
            .getAvailableLinkPlugins()
            .stream()
            .filter(p -> p.getSupportedLinkTypes().contains(linkType))
            .toList();
    }

    public Optional<LinkPlugin> getLinkPluginByName(String linkImpl) {
        return linkManager.getLinkPluginByName(linkImpl);
    }

    public void createNewLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        dcLinkPersistenceService.addLinkConfiguration(linkConfiguration);
    }

    public void createNewLinkPartner(DomibusConnectorLinkPartner value) {
        dcLinkPersistenceService.addLinkPartner(value);
    }

    public void deleteLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        dcLinkPersistenceService.deleteLinkConfiguration(linkConfiguration);
    }
}
