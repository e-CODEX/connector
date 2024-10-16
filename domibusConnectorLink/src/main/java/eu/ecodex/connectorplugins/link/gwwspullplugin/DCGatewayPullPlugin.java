/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.gwwspullplugin;

import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.ActiveLink;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.api.PluginFeature;
import eu.ecodex.connector.link.service.PullFromLinkPartner;
import eu.ecodex.connector.link.service.SubmitToLinkPartner;
import eu.ecodex.connector.link.utils.LinkPluginUtils;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The DCGatewayPullPlugin class is an implementation of the LinkPlugin interface that handles
 * configuration and management of the Gateway Pull Plugin for Domibus.
 */
public class DCGatewayPullPlugin implements LinkPlugin {
    private static final Logger LOGGER = LogManager.getLogger(DCGatewayPullPlugin.class);
    public static final String IMPL_NAME = "gwwspullplugin";
    @Autowired
    ConfigurableApplicationContext applicationContext;
    @Autowired
    Scheduler scheduler;
    private SubmitToLinkPartner submitToLink;
    private PullFromLinkPartner pullFromLink;

    @Override
    public boolean canHandle(String implementation) {
        return getPluginName().equals(implementation);
    }

    @Override
    public String getPluginName() {
        return IMPL_NAME;
    }

    @Override
    public ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {

        LOGGER.info("Starting Configuration for [{}]", linkConfiguration);

        ConfigurableApplicationContext childCtx =
            LinkPluginUtils.getChildContextBuilder(applicationContext)
                           .withDomibusConnectorLinkConfiguration(linkConfiguration)
                           .withSources(DCGatewayPullPluginConfiguration.class)
                           .withProfiles(
                               DCGatewayPullPluginConfiguration.DC_GATEWAY_PULL_PLUGIN_PROFILE)
                           .run();

        this.submitToLink = childCtx.getBean(SubmitToLinkPartner.class);

        var activeLink = new ActiveLink();
        activeLink.setLinkConfiguration(linkConfiguration);
        activeLink.setChildContext(childCtx);
        activeLink.setSubmitToLink(submitToLink);
        activeLink.setLinkPlugin(this);

        this.pullFromLink = childCtx.getBean(PullFromLinkPartner.class);

        return activeLink;
    }

    @Override
    public void shutdownConfiguration(ActiveLink activeLink) {
        ConfigurableApplicationContext childContext = activeLink.getChildContext();
        if (childContext != null) {
            childContext.close();
        }
    }

    @Override
    public ActiveLinkPartner enableLinkPartner(
        DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink) {
        var activeLinkPartner = new ActiveLinkPartner();
        activeLinkPartner.setParentLink(activeLink);
        activeLinkPartner.setLinkPartner(linkPartner);
        return activeLinkPartner;
    }

    @Override
    public void shutdownActiveLinkPartner(ActiveLinkPartner linkPartner) {
        this.shutdownConfiguration(linkPartner.getParentLink());
    }

    @Override
    public SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner) {
        return this.submitToLink;
    }

    @Override
    public Optional<PullFromLinkPartner> getPullFromLink(ActiveLinkPartner activeLinkPartner) {
        return Optional.of(this.pullFromLink);
    }

    @Override
    public List<PluginFeature> getFeatures() {
        return Stream.of(
                         PluginFeature.RCV_PULL_MODE,
                         PluginFeature.SEND_PUSH_MODE,
                         PluginFeature.GATEWAY_PLUGIN,
                         PluginFeature.SUPPORTS_LINK_PARTNER_SHUTDOWN,
                         PluginFeature.SUPPORTS_LINK_SHUTDOWN
                     )
                     .toList();
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return Stream.of(DCGatewayPullPluginConfigurationProperties.class)
                     .collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPartnerConfigurationProperties() {
        return Collections.emptyList();
    }

    @Override
    public Set<LinkType> getSupportedLinkTypes() {
        return Stream.of(LinkType.GATEWAY).collect(Collectors.toSet());
    }
}
