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
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@ConditionalOnBean(DCLinkPluginConfiguration.class)
public class DCLinkFacade {
    private final DCActiveLinkManagerService linkManager;
    private final DCLinkPersistenceService dcLinkPersistenceService;
    private final DCLinkPluginConfigurationProperties lnkConfig;

    public DCLinkFacade(
            DCActiveLinkManagerService linkManager,
            DCLinkPersistenceService dcLinkPersistenceService,
            DCLinkPluginConfigurationProperties props) {
        this.linkManager = linkManager;
        this.dcLinkPersistenceService = dcLinkPersistenceService;
        this.lnkConfig = props;
    }

    public boolean isActive(DomibusConnectorLinkPartner d) {
        if (d.getLinkPartnerName() == null) {
            throw new IllegalArgumentException("LinkPartner name is not allowed to be null!");
        }
        return linkManager.getActiveLinkPartnerByName(d.getLinkPartnerName()).isPresent();
    }

    public void shutdownLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        linkManager.shutdownLinkPartner(linkPartner.getLinkPartnerName());
    }

    public List<DomibusConnectorLinkPartner> getAllLinksOfType(LinkType linkType) {
        return getAllLinks().stream().filter(l -> Objects.equals(l.getLinkType(), linkType) || linkType == null)
                            .collect(Collectors.toList());
    }

    public List<DomibusConnectorLinkPartner> getAllLinks() {
        List<DomibusConnectorLinkPartner> allLinks = new ArrayList<>();

        allLinks.addAll(dcLinkPersistenceService.getAllLinks());
        allLinks.addAll(lnkConfig.getBackend().stream().filter(Objects::nonNull)
                                 .flatMap(b -> mapCnfg(b, LinkType.BACKEND)).collect(Collectors.toList()));
        allLinks.addAll(mapCnfg(lnkConfig.getGateway(), LinkType.GATEWAY).collect(Collectors.toList()));
        return allLinks;
    }

    private Stream<DomibusConnectorLinkPartner> mapCnfg(
            DCLinkPluginConfigurationProperties.DCLnkPropertyConfig b, LinkType linkType) {
        if (b == null) {
            return Stream.empty();
        }
        DomibusConnectorLinkConfiguration linkConfig = new DomibusConnectorLinkConfiguration();
        if (b.getLinkConfig() != null) {
            BeanUtils.copyProperties(b.getLinkConfig(), linkConfig);
            linkConfig.setProperties(mapKebabCaseToCamelCase(b.getLinkConfig().getProperties()));
        }
        linkConfig.setConfigurationSource(ConfigurationSource.ENV);
        return b.getLinkPartners().stream().map(p -> {
            DomibusConnectorLinkPartner p1 = new DomibusConnectorLinkPartner();
            BeanUtils.copyProperties(p, p1);
            p1.setConfigurationSource(ConfigurationSource.ENV);
            p1.setLinkConfiguration(linkConfig);
            p1.setLinkType(linkType);
            p1.setProperties(mapKebabCaseToCamelCase(p.getProperties()));
            return p1;
        });
    }

    // convert property names from KebabCase to CamelCase
    // eg.:  cn-name to cnName
    private Map<String, String> mapKebabCaseToCamelCase(Map<String, String> properties) {
        return properties
                .entrySet().stream()
                .collect(Collectors.toMap(e -> CaseFormat.LOWER_HYPHEN.to(
                        CaseFormat.LOWER_CAMEL,
                        e.getKey()
                ), e -> e.getValue()));
    }

    public Optional<DomibusConnectorLinkPartner> loadLinkPartner(DomibusConnectorLinkPartner.LinkPartnerName name) {
        return getAllLinks().stream().filter(l -> name.equals(l.getLinkPartnerName())).findAny();
    }

    public void startLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        Optional<ActiveLinkPartner> activeLinkPartner = this.linkManager.activateLinkPartner(linkPartner);
        if (!activeLinkPartner.isPresent()) {
            throw new LinkPluginException("Start failed!");
        }
    }

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

    public void updateLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        if (linkPartner.getConfigurationSource() == ConfigurationSource.DB) {
            dcLinkPersistenceService.updateLinkPartner(linkPartner);
        }
    }

    public Optional<DomibusConnectorLinkConfiguration> loadLinkConfig(DomibusConnectorLinkConfiguration.LinkConfigName configName) {
        List<DomibusConnectorLinkConfiguration> allConfigs = getAllConfigurations();
        return allConfigs.stream().filter(c -> configName.equals(c.getConfigName())).findAny();
    }

    private List<DomibusConnectorLinkConfiguration> getAllConfigurations() {
        List<DomibusConnectorLinkConfiguration> allLinkConfigurations =
                dcLinkPersistenceService.getAllLinkConfigurations();

        return Stream.of(
                getAllLinks().stream().map(DomibusConnectorLinkPartner::getLinkConfiguration),
                allLinkConfigurations.stream()
        ).flatMap(Function.identity()).distinct().collect(Collectors.toList());
    }

    public void updateLinkConfig(DomibusConnectorLinkConfiguration linkConfig) {
        if (linkConfig.getConfigurationSource() == ConfigurationSource.DB) {
            dcLinkPersistenceService.updateLinkConfig(linkConfig);
        }
    }

    public List<DomibusConnectorLinkConfiguration> getAllLinkConfigurations(LinkType linkType) {
        // get all configurations wich support the linkType
        Stream<DomibusConnectorLinkConfiguration> stream1 =
                dcLinkPersistenceService.getAllLinkConfigurations().stream()
                                        .filter(c -> getLinkPluginByName(c.getLinkImpl())
                                                .map(p -> p.getFeatures()
                                                           .contains(
                                                                   getPluginFeatureFromLinkType(linkType)))
                                                .orElse(false));
        // get all configurations which have a link partner with this link type
        Stream<DomibusConnectorLinkConfiguration> stream2 =
                getAllLinksOfType(linkType).stream().map(DomibusConnectorLinkPartner::getLinkConfiguration);
        // merge together
        return Stream.of(stream1, stream2).flatMap(Function.identity()).distinct().collect(Collectors.toList());
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

    public List<LinkPlugin> getAvailableLinkPlugins(LinkType linkType) {
        return linkManager.getAvailableLinkPlugins().stream().filter(p -> p.getSupportedLinkTypes().contains(linkType))
                          .collect(Collectors.toList());
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
