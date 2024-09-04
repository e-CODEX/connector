/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.wsbackendplugin;

import static eu.ecodex.connector.tools.logging.LoggingMarker.Log4jMarker.CONFIG;

import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.ActiveLink;
import eu.ecodex.connector.link.api.ActiveLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.api.PluginFeature;
import eu.ecodex.connector.link.api.exception.LinkPluginException;
import eu.ecodex.connector.link.service.SubmitToLinkPartner;
import eu.ecodex.connector.link.utils.LinkPluginUtils;
import eu.ecodex.connectorplugins.link.wsbackendplugin.childctx.WsActiveLinkPartnerManager;
import eu.ecodex.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginConfiguration;
import eu.ecodex.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginConfigurationProperties;
import eu.ecodex.connectorplugins.link.wsbackendplugin.childctx.WsBackendPluginLinkPartnerConfigurationProperties;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.Validator;

/**
 * The WsBackendPlugin class is an implementation of the LinkPlugin interface. It provides
 * functionality for starting and shutting down configurations, enabling and shutting down link
 * partners, and getting features and configuration properties for the plugin.
 */
public class WsBackendPlugin implements LinkPlugin {
    private static final Logger LOGGER = LogManager.getLogger(WsBackendPlugin.class);
    public static final String IMPL_NAME = "wsbackendplugin";
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
        //        LOGGER.debug("Using Properties")

        ConfigurableApplicationContext childCtx =
            LinkPluginUtils.getChildContextBuilder(applicationContext)
                           .withDomibusConnectorLinkConfiguration(linkConfiguration)
                           .withSources(WsBackendPluginConfiguration.class)
                           .withProfiles(
                               WsBackendPluginConfiguration.WS_BACKEND_PLUGIN_PROFILE_NAME)
                           .run();

        var activeLink = new ActiveLink();
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
    public ActiveLinkPartner enableLinkPartner(
        DomibusConnectorLinkPartner linkPartner, ActiveLink activeLink) {
        LOGGER.debug("Enabling LinkPartner [{}]", linkPartner);
        Map<String, String> properties = linkPartner.getProperties();

        LOGGER.debug(
            "Binding properties [{}] to linkPartnerConfig [{}]", properties,
            WsBackendPluginLinkPartnerConfigurationProperties.class
        );

        var validationBindHandler = new ValidationBindHandler(validator);

        var binder = new Binder(new MapConfigurationPropertySource(properties));
        BindResult<WsBackendPluginLinkPartnerConfigurationProperties> bindingResult =
            binder.bind("", Bindable.of(WsBackendPluginLinkPartnerConfigurationProperties.class),
                        validationBindHandler
            );
        if (!bindingResult.isBound()) {
            var error = String.format(
                "Binding properties [%s] to linkPartnerConfig [%s] failed",
                properties,
                WsBackendPluginLinkPartnerConfigurationProperties.class
            );
            throw new LinkPluginException(error);
        }

        ConfigurableApplicationContext childContext = activeLink.getChildContext();
        if (childContext == null) {
            throw new LinkPluginException(
                "Cannot start LinkPartner, because context of Link is null");
        }
        var linkPartnerConfig = bindingResult.get();
        var activeLinkPartner = new WsBackendPluginActiveLinkPartner();
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
        if (linkPartner instanceof WsBackendPluginActiveLinkPartner activeLinkPartner) {
            if (linkPartner.getChildContext().isPresent()) {
                ConfigurableApplicationContext ctx = linkPartner.getChildContext().get();
                WsActiveLinkPartnerManager bean = ctx.getBean(WsActiveLinkPartnerManager.class);
                bean.deregister(activeLinkPartner);
            } else {
                throw new RuntimeException("Error no context found!");
            }
        } else {
            throw new IllegalArgumentException("The provided link partner is not of type "
                                                   + WsBackendPluginActiveLinkPartner.class);
        }
    }

    @Override
    public SubmitToLinkPartner getSubmitToLink(ActiveLinkPartner linkPartner) {
        return linkPartner.getChildContext().map(ctx -> ctx.getBean(SubmitToLinkPartner.class))
                          .orElse(null);
    }

    @Override
    public List<PluginFeature> getFeatures() {
        return Stream
            .of(
                PluginFeature.RCV_PASSIVE_MODE,
                PluginFeature.SEND_PUSH_MODE,
                PluginFeature.SEND_PASSIVE_MODE,
                PluginFeature.SUPPORTS_LINK_PARTNER_SHUTDOWN,
                PluginFeature.BACKEND_PLUGIN,
                PluginFeature.SUPPORTS_MULTIPLE_PARTNERS
            )
            .toList();
    }

    @Override
    public List<Class<?>> getPluginConfigurationProperties() {
        return Stream.of(WsBackendPluginConfigurationProperties.class)
                     .collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> getPartnerConfigurationProperties() {
        return Stream.of(WsBackendPluginLinkPartnerConfigurationProperties.class)
                     .collect(Collectors.toList());
    }

    @Override
    public Set<LinkType> getSupportedLinkTypes() {
        return Stream.of(LinkType.BACKEND).collect(Collectors.toSet());
    }
}
