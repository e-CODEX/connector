/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.gwwspushplugin;

import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.ActiveLink;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.api.PluginFeature;
import eu.ecodex.connector.link.service.SubmitToLinkPartner;
import eu.ecodex.connector.link.utils.LinkPluginUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Represents a plugin for the WS Gateway in Domibus.
 */
@SuppressWarnings("squid:S6813")
public class WsGatewayPlugin implements LinkPlugin {
    public static final String IMPL_NAME = "gwwspushplugin";
    @Autowired
    ConfigurableApplicationContext applicationContext;
    private SubmitToLinkPartner submitToLink;

    @Override
    public ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        var activeLink = new ActiveLink();
        activeLink.setLinkConfiguration(linkConfiguration);
        activeLink.setLinkPlugin(this);
        return activeLink;
    }

    @Override
    public String getPluginName() {
        return IMPL_NAME;
    }

    @Override
    public void shutdownConfiguration(ActiveLink activeLink) {
        throw new RuntimeException("not supported yet!");
    }

    @Override
    public synchronized ActiveLinkPartner enableLinkPartner(
        DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink) {

        if (submitToLink != null) {
            throw new RuntimeException("Plugin already activated!");
        }

        var activeLinkPartner = new ActiveLinkPartner();
        activeLinkPartner.setLinkPartner(linkPartner);
        activeLinkPartner.setParentLink(activeLink);

        ConfigurableApplicationContext childCtx =
            LinkPluginUtils.getChildContextBuilder(applicationContext)
                           .withDomibusConnectorLinkConfiguration(activeLink.getLinkConfiguration())
                           .withDomibusConnectorLinkPartner(linkPartner)
                           .withSources(WsGatewayPluginConfiguration.class)
                           .withProfiles(WsGatewayPluginConfiguration.WS_GATEWAY_PLUGIN)
                           .run();

        activeLinkPartner.setChildContext(childCtx);

        submitToLink = childCtx.getBean(SubmitToLinkPartner.class);
        activeLinkPartner.setSubmitToLink(submitToLink);

        return activeLinkPartner;
    }

    @Override
    public void shutdownActiveLinkPartner(ActiveLinkPartner linkPartner) {
        throw new RuntimeException("not supported yet!");
    }

    @Override
    public SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner) {
        return submitToLink;
    }

    @Override
    public List<PluginFeature> getFeatures() {
        return Stream
            .of(
                PluginFeature.RCV_PASSIVE_MODE,
                PluginFeature.GATEWAY_PLUGIN,
                PluginFeature.SEND_PUSH_MODE
            )
            .toList();
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return Stream.of(WsGatewayPluginConfigurationProperties.class)
                     .collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPartnerConfigurationProperties() {
        return new ArrayList<>();
    }

    @Override
    public Set<LinkType> getSupportedLinkTypes() {
        return Stream.of(LinkType.GATEWAY).collect(Collectors.toSet());
    }
}
