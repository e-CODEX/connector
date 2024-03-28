package eu.domibus.connector.link.api;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.PullFromLinkPartner;
import eu.domibus.connector.link.service.SubmitToLinkPartner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Must be implemented by a link plugin
 */
public interface LinkPlugin {
    default String getPluginName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    default String getPluginDescription() {
        return this.toString();
    }

    /**
     * @param implementation - the implementation name
     * @return true if the PluginFactory can handle provide the implementation
     */
    default boolean canHandle(String implementation) {
        return getPluginName().equals(implementation);
    }

    /**
     * @param linkConfiguration - the link configuration
     * @return the active Plugin
     */
    ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration);

    void shutdownConfiguration(ActiveLink activeLink);

    ActiveLinkPartner enableLinkPartner(DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink);

    void shutdownActiveLinkPartner(ActiveLinkPartner linkPartner);

    SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner);

    /**
     * @return a list of the supported Features of this plugin
     */
    List<PluginFeature> getFeatures();

    /**
     * @return a list of with @ConfigurationProperties annotated classes
     * which represents the plugin properties
     */
    default List<Class<?>> getPluginConfigurationProperties() {
        return Collections.emptyList();
    }

    default List<Class<?>> getPartnerConfigurationProperties() {
        return Collections.emptyList();
    }

    default Optional<PullFromLinkPartner> getPullFromLink(ActiveLinkPartner activeLinkPartner) {
        return Optional.empty();
    }

    default Set<LinkType> getSupportedLinkTypes() {
        return Stream.of(LinkType.values()).collect(Collectors.toSet());
    }
}
