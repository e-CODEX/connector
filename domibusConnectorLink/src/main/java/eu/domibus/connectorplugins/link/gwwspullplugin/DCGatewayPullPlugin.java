package eu.domibus.connectorplugins.link.gwwspullplugin;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLink;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.service.PullFromLinkPartner;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.link.utils.LinkPluginUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DCGatewayPullPlugin implements LinkPlugin {
    public static final String IMPL_NAME = "gwwspullplugin";
    private final static Logger LOGGER = LogManager.getLogger(DCGatewayPullPlugin.class);

    @Autowired
    ConfigurableApplicationContext applicationContext;
    @Autowired
    Scheduler scheduler;
    private SubmitToLinkPartner submitToLink;
    private PullFromLinkPartner pullFromLink;

    @Override
    public String getPluginName() {
        return IMPL_NAME;
    }

    @Override
    public boolean canHandle(String implementation) {
        return getPluginName().equals(implementation);
    }

    @Override
    public ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        LOGGER.info("Starting Configuration for [{}]", linkConfiguration);

        ConfigurableApplicationContext childCtx =
                LinkPluginUtils.getChildContextBuilder(applicationContext)
                               .withDomibusConnectorLinkConfiguration(
                                       linkConfiguration
                               )
                               .withSources(DCGatewayPullPluginConfiguration.class)
                               .withProfiles(DCGatewayPullPluginConfiguration.DC_GATEWAY_PULL_PLUGIN_PROFILE)
                               .run();

        this.submitToLink = childCtx.getBean(SubmitToLinkPartner.class);

        ActiveLink activeLink = new ActiveLink();
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
    public ActiveLinkPartner enableLinkPartner(DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink) {
        ActiveLinkPartner activeLinkPartner = new ActiveLinkPartner();
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
    public List<PluginFeature> getFeatures() {
        return Stream.of(
                PluginFeature.RCV_PULL_MODE,
                PluginFeature.SEND_PUSH_MODE,
                PluginFeature.GATEWAY_PLUGIN,
                PluginFeature.SUPPORTS_LINK_PARTNER_SHUTDOWN,
                PluginFeature.SUPPORTS_LINK_SHUTDOWN
        ).collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return Stream.of(DCGatewayPullPluginConfigurationProperties.class).collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPartnerConfigurationProperties() {
        return Collections.emptyList();
    }

    @Override
    public Optional<PullFromLinkPartner> getPullFromLink(ActiveLinkPartner activeLinkPartner) {
        return Optional.of(this.pullFromLink);
    }

    @Override
    public Set<LinkType> getSupportedLinkTypes() {
        return Stream.of(LinkType.GATEWAY).collect(Collectors.toSet());
    }
}
