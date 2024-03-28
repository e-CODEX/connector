package eu.domibus.connectorplugins.link.wsbackendplugin;

import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLink;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.link.utils.LinkPluginUtils;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsActiveLinkPartnerManager;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginConfiguration;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginConfigurationProperties;
import eu.domibus.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginLinkPartnerConfigurationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.domibus.connector.tools.logging.LoggingMarker.Log4jMarker.CONFIG;


public class WsBackendPlugin implements LinkPlugin {
    public static final String IMPL_NAME = "wsbackendplugin";
    private static final Logger LOGGER = LogManager.getLogger(WsBackendPlugin.class);
    private final ConfigurableApplicationContext applicationContext;
    private final Validator validator;

    public WsBackendPlugin(ConfigurableApplicationContext applicationContext, Validator validator) {
        this.applicationContext = applicationContext;
        this.validator = validator;
    }

    @Override
    public String getPluginName() {
        return IMPL_NAME;
    }

    @Override
    public ActiveLink startConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        LOGGER.info("Starting Configuration for [{}]", linkConfiguration);
        // LOGGER.debug("Using Properties")

        ConfigurableApplicationContext childCtx = LinkPluginUtils
                .getChildContextBuilder(applicationContext)
                .withDomibusConnectorLinkConfiguration(linkConfiguration)
                .withSources(WsBackendPluginConfiguration.class)
                .withProfiles(WsBackendPluginConfiguration.WS_BACKEND_PLUGIN_PROFILE_NAME)
                .run();

        ActiveLink activeLink = new ActiveLink();
        activeLink.setLinkConfiguration(linkConfiguration);
        activeLink.setChildContext(childCtx);

        return activeLink;
    }

    @Override
    public void shutdownConfiguration(ActiveLink activeLink) {
        if (activeLink.getChildContext() != null) {
            activeLink.getChildContext().close();
        }
    }

    @Override
    public ActiveLinkPartner enableLinkPartner(DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink) {
        LOGGER.debug("Enabling LinkPartner [{}]", linkPartner);
        Map<String, String> properties = linkPartner.getProperties();

        LOGGER.debug(
                "Binding properties [{}] to linkPartnerConfig [{}]",
                properties,
                WsBackendPluginLinkPartnerConfigurationProperties.class
        );

        ValidationBindHandler validationBindHandler = new ValidationBindHandler(validator);

        Binder binder = new Binder(new MapConfigurationPropertySource(properties));
        BindResult<WsBackendPluginLinkPartnerConfigurationProperties> bindingResult =
                binder.bind(
                        "",
                        Bindable.of(WsBackendPluginLinkPartnerConfigurationProperties.class),
                        validationBindHandler
                );
        if (!bindingResult.isBound()) {
            String error = String.format(
                    "Binding properties [%s] to linkPartnerConfig [%s] failed",
                    properties,
                    WsBackendPluginLinkPartnerConfigurationProperties.class
            );
            throw new LinkPluginException(error);
        }

        WsBackendPluginLinkPartnerConfigurationProperties linkPartnerConfig = bindingResult.get();

        ConfigurableApplicationContext childContext = activeLink.getChildContext();
        if (childContext == null) {
            throw new LinkPluginException("Cannot start LinkPartner, because context of Link is null");
        }
        WsBackendPluginActiveLinkPartner activeLinkPartner = new WsBackendPluginActiveLinkPartner();
        activeLinkPartner.setLinkPartner(linkPartner);
        activeLinkPartner.setChildContext(childContext);
        activeLinkPartner.setParentLink(activeLink);
        activeLinkPartner.setConfig(linkPartnerConfig);
        activeLinkPartner.setSubmitToLink(childContext.getBean(SubmitToLinkPartner.class));

        // register certificate DN for authentication
        WsActiveLinkPartnerManager bean = childContext.getBean(WsActiveLinkPartnerManager.class);
        bean.registerDn(activeLinkPartner);

        LOGGER.info(CONFIG, "Successfully enabled LinkPartner [{}]", linkPartner);
        return activeLinkPartner;
    }

    @Override
    public void shutdownActiveLinkPartner(ActiveLinkPartner linkPartner) {
        if (linkPartner instanceof WsBackendPluginActiveLinkPartner) {
            WsBackendPluginActiveLinkPartner lp = (WsBackendPluginActiveLinkPartner) linkPartner;
            if (linkPartner.getChildContext().isPresent()) {
                ConfigurableApplicationContext ctx = linkPartner.getChildContext().get();
                WsActiveLinkPartnerManager bean = ctx.getBean(WsActiveLinkPartnerManager.class);
                bean.deregister(lp);
            } else {
                throw new RuntimeException("Error no context found!");
            }
        } else {
            throw new IllegalArgumentException(
                    "The provided link partner is not of type " + WsBackendPluginActiveLinkPartner.class
            );
        }
    }

    @Override
    public SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner) {
        return linkPartner.getChildContext().map(ctx -> ctx.getBean(SubmitToLinkPartner.class)).orElse(null);
    }

    @Override
    public List<PluginFeature> getFeatures() {
        return Stream.of(
                PluginFeature.RCV_PASSIVE_MODE,
                PluginFeature.SEND_PUSH_MODE,
                PluginFeature.SEND_PASSIVE_MODE,
                PluginFeature.SUPPORTS_LINK_PARTNER_SHUTDOWN,
                PluginFeature.BACKEND_PLUGIN,
                PluginFeature.SUPPORTS_MULTIPLE_PARTNERS
        ).collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return Stream.of(WsBackendPluginConfigurationProperties.class).collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPartnerConfigurationProperties() {
        return Stream.of(WsBackendPluginLinkPartnerConfigurationProperties.class).collect(Collectors.toList());
    }

    @Override
    public Set<LinkType> getSupportedLinkTypes() {
        return Stream.of(LinkType.BACKEND).collect(Collectors.toSet());
    }
}
