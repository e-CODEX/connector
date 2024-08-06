/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import eu.domibus.connector.link.common.DefaultWsCallbackHandler;
import eu.domibus.connector.link.common.MerlinPropertiesFactory;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWSService;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the spring childContext for the pullGatewayPlugin.
 */
@Configuration
@EnableConfigurationProperties(WsBackendPluginConfigurationProperties.class)
@ComponentScan(basePackageClasses = WsBackendPluginConfiguration.class)
public class WsBackendPluginConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(WsBackendPluginConfiguration.class);
    public static final String WS_BACKEND_PLUGIN_PROFILE_NAME = "link.wsbackendplugin";
    private final WsBackendPluginConfigurationProperties configurationProperties;
    private final SpringBus springBus;
    private final MerlinPropertiesFactory merlinPropertiesFactory;

    /**
     * Instantiates a new WsBackendPluginConfiguration object.
     *
     * @param configurationProperties the backend plugin configuration properties
     * @param springBus               the spring bus object
     * @param merlinPropertiesFactory the merlin properties factory object
     */
    public WsBackendPluginConfiguration(
        WsBackendPluginConfigurationProperties configurationProperties,
        SpringBus springBus,
        MerlinPropertiesFactory merlinPropertiesFactory) {
        this.configurationProperties = configurationProperties;
        this.springBus = springBus;
        this.merlinPropertiesFactory = merlinPropertiesFactory;
    }

    @Bean
    WsBackendServiceEndpointImpl wsBackendServiceEndpoint() {
        return new WsBackendServiceEndpointImpl();
    }

    @Bean
    EndpointImpl connectorBackendWS() {
        WsBackendPluginConfigurationProperties config = configurationProperties;
        var endpoint = new EndpointImpl(springBus, wsBackendServiceEndpoint());
        endpoint.setAddress(config.getBackendPublishAddress());
        endpoint.setServiceName(DomibusConnectorBackendWSService.SERVICE);
        endpoint.setEndpointName(
            DomibusConnectorBackendWSService.DomibusConnectorBackendWebService);
        endpoint.setWsdlLocation("classpath:wsdl/DomibusConnectorBackendWebService.wsdl");

        var wsPolicyFeature = new WsPolicyLoader(config.getWsPolicy()).loadPolicyFeature();
        endpoint.getFeatures().add(wsPolicyFeature);

        if (configurationProperties.isCxfLoggingEnabled()) {
            endpoint.getFeatures().add(new LoggingFeature());
        }

        endpoint.getProperties().put("security.callback-handler", new DefaultWsCallbackHandler());
        endpoint.getProperties().put("security.store.bytes.in.attachment", true);
        endpoint.getProperties().put("security.enable.streaming", true);
        endpoint.getProperties().put("mtom-enabled", true);

        var encSigProperties =
            merlinPropertiesFactory.mapCertAndStoreConfigPropertiesToMerlinProperties(
                configurationProperties.getSoap(), ""
            );

        endpoint.getProperties().put("security.encryption.properties", encSigProperties);
        endpoint.getProperties().put("security.signature.properties", encSigProperties);
        endpoint.getProperties().put("security.encryption.username", "useReqSigCert");

        endpoint.publish();
        LOGGER.debug(
            "Published WebService [{}] under [{}]", DomibusConnectorBackendWSService.class,
            config.getBackendPublishAddress()
        );
        return endpoint;
    }
}
