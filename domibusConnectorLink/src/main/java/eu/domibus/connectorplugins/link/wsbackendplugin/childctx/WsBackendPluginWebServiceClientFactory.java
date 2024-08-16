/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import static eu.domibus.connector.tools.logging.LoggingMarker.Log4jMarker.CONFIG;

import eu.domibus.connector.link.common.MerlinPropertiesFactory;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import java.util.HashMap;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Creates a web service client for pushing messages to backend client.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Component
public class WsBackendPluginWebServiceClientFactory {
    private static final Logger LOGGER =
        LogManager.getLogger(WsBackendPluginWebServiceClientFactory.class);
    private final WsBackendPluginConfigurationProperties config;
    private final MerlinPropertiesFactory merlinPropertiesFactory;

    public WsBackendPluginWebServiceClientFactory(
        WsBackendPluginConfigurationProperties config,
        MerlinPropertiesFactory merlinPropertiesFactory) {
        this.config = config;
        this.merlinPropertiesFactory = merlinPropertiesFactory;
    }

    /**
     * Creates a web service client for pushing messages to a backend client.
     *
     * @param linkPartner The active link partner for the WS backend plugin.
     * @return The created web service client.
     */
    public DomibusConnectorBackendDeliveryWebService createBackendWsClient(
        WsBackendPluginActiveLinkPartner linkPartner) {
        LOGGER.debug(
            CONFIG, "#createWsClient: creating WS endpoint for backendClient [{}]", linkPartner);
        WsBackendPluginLinkPartnerConfigurationProperties linkPartnerConfig =
            linkPartner.getConfig();
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorBackendDeliveryWebService.class);

        if (config.isCxfLoggingEnabled()) {
            jaxWsProxyFactoryBean.getFeatures().add(new LoggingFeature());
        }

        var pushAddress = linkPartnerConfig.getPushAddress();
        var wsPolicyFeature = new WsPolicyLoader(config.getWsPolicy()).loadPolicyFeature();
        jaxWsProxyFactoryBean.getFeatures().add(wsPolicyFeature);
        jaxWsProxyFactoryBean.setAddress(pushAddress);
        jaxWsProxyFactoryBean.setWsdlURL(
            DomibusConnectorBackendDeliveryWSService.WSDL_LOCATION.toString());
        // jaxWsProxyFactoryBean.setWsdlURL(pushAddress + "?wsdl");
        // maybe load own wsdl instead of remote one?

        var properties = merlinPropertiesFactory.mapCertAndStoreConfigPropertiesToMerlinProperties(
            this.config.getSoap(), "."
        );

        HashMap<String, Object> jaxWsFactoryBeanProperties = new HashMap<>();
        jaxWsFactoryBeanProperties.put(
            "security.encryption.username", linkPartnerConfig.getEncryptionAlias());
        jaxWsFactoryBeanProperties.put("mtom-enabled", true);
        jaxWsFactoryBeanProperties.put("security.encryption.properties", properties);
        jaxWsFactoryBeanProperties.put("security.signature.properties", properties);

        LOGGER.debug(
            CONFIG, "#createWsClient: Configuring WsClient with following properties: [{}]",
            jaxWsFactoryBeanProperties
        );

        jaxWsProxyFactoryBean.setProperties(jaxWsFactoryBeanProperties);

        return (DomibusConnectorBackendDeliveryWebService) jaxWsProxyFactoryBean.create();
    }
}
