/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connectorplugins.link.testbackend;

import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLink;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
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
