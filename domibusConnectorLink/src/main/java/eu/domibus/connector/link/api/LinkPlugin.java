/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

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
 * Must be implemented by a link plugin.
 */
public interface LinkPlugin {
    default String getPluginName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    default String getPluginDescription() {
        return this.toString();
    }

    /**
     * Determines whether the given implementation can be handled by the LinkPlugin.
     *
     * @param implementation the implementation name.
     * @return true if the PluginFactory can be handled by the LinkPlugin, false otherwise
     */
    default boolean canHandle(String implementation) {
        return getPluginName().equals(implementation);
    }

    /**
     * Starts the configuration for a given DomibusConnectorLinkConfiguration.
     *
     * @param linkConfiguration the configuration for the link connector.
     * @return an ActiveLink representing the active link configuration.
     */
    ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration);

    void shutdownConfiguration(ActiveLink activeLink);

    ActiveLinkPartner enableLinkPartner(
        DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink);

    void shutdownActiveLinkPartner(ActiveLinkPartner linkPartner);

    SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner);

    /**
     * Retrieves the list of features supported by the LinkPlugin.
     *
     * @return a list of PluginFeature enum values representing the supported features.
     */
    List<PluginFeature> getFeatures();

    /**
     * Retrieves the list of plugin configuration properties supported by the LinkPlugin.
     *
     * @return a list of Class objects representing the plugin configuration properties.
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
