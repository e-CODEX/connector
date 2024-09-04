/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.testbackend;

import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.ActiveLink;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.api.PluginFeature;
import eu.ecodex.connector.link.service.SubmitToLinkPartner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/**
 * The testbackendPlugin acts as a special plugin. This plugin does not transport any message to
 * another service/gateway/backend/... It's only purpose is to respond with a delivery evidence for
 * any received message.
 */
//@Profile("plugin-" + TestbackendPlugin.IMPL_NAME)
@Component
public class TestbackendPlugin implements LinkPlugin {
    public static final String IMPL_NAME = "testbackend";
    private final SubmitToTestLink submitToTestLink;

    public TestbackendPlugin(SubmitToTestLink submitToTestLink) {
        this.submitToTestLink = submitToTestLink;
    }

    @Override
    public String getPluginName() {
        return IMPL_NAME;
    }

    @Override
    public String getPluginDescription() {
        return "Domibus Connector Testbackend";
    }

    @Override
    public ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        var activeLink = new ActiveLink();
        activeLink.setLinkConfiguration(linkConfiguration);
        activeLink.setSubmitToLink(submitToTestLink);
        return activeLink;
    }

    @SuppressWarnings("squid:S1135")
    @Override
    public void shutdownConfiguration(ActiveLink activeLink) {
        // TODO figure out why this method body is empty
    }

    @Override
    public ActiveLinkPartner enableLinkPartner(
        DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink) {
        var activeLinkPartner = new ActiveLinkPartner();
        activeLinkPartner.setLinkPartner(linkPartner);
        activeLinkPartner.setParentLink(activeLink);
        activeLinkPartner.setSubmitToLink(submitToTestLink);
        submitToTestLink.setDomibusConnectorLinkPartner(linkPartner);
        submitToTestLink.setEnabled(true);
        return activeLinkPartner;
    }

    @Override
    public void shutdownActiveLinkPartner(ActiveLinkPartner linkPartner) {
        submitToTestLink.setEnabled(false);
    }

    @Override
    public SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner) {
        return submitToTestLink;
    }

    @Override
    public List<PluginFeature> getFeatures() {
        return Stream.of(
            PluginFeature.RCV_PASSIVE_MODE
        ).toList();
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return new ArrayList<>();
    }

    @Override
    public List<Class<?>> getPartnerConfigurationProperties() {
        return new ArrayList<>();
    }
}
