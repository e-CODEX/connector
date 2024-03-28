package eu.domibus.connectorplugins.link.gwwspushplugin;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLink;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.link.utils.LinkPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WsGatewayPlugin implements LinkPlugin {
    public static final String IMPL_NAME = "gwwspushplugin";

    @Autowired
    ConfigurableApplicationContext applicationContext;
    private SubmitToLinkPartner submitToLink;

    @Override
    public ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        ActiveLink activeLink = new ActiveLink();
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

        ActiveLinkPartner activeLinkPartner = new ActiveLinkPartner();
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
        return Stream.of(PluginFeature.RCV_PASSIVE_MODE, PluginFeature.GATEWAY_PLUGIN, PluginFeature.SEND_PUSH_MODE)
                     .collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return Stream.of(WsGatewayPluginConfigurationProperties.class).collect(Collectors.toList());
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
