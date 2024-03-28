package eu.domibus.connectorplugins.link.gwwspushplugin;

import eu.domibus.connector.link.common.DefaultWsCallbackHandler;
import eu.domibus.connector.link.common.MerlinPropertiesFactory;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWSService;
import eu.domibus.connector.ws.gateway.submission.webservice.DomibusConnectorGatewaySubmissionWebService;
import eu.domibus.connector.ws.gateway.webservice.DomibusConnectorGatewayWSService;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Configuration for the spring childContext for
 * the pushGatewayPlugin
 */
@Configuration
@Profile(WsGatewayPluginConfiguration.WS_GATEWAY_PLUGIN)
@EnableConfigurationProperties(WsGatewayPluginConfigurationProperties.class)
@ComponentScan(basePackageClasses = WsGatewayPluginConfiguration.class)
public class WsGatewayPluginConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(WsGatewayPluginConfiguration.class);
    public static final String WS_GATEWAY_PLUGIN = "link.wsgatewayplugin";

    @Autowired
    WsGatewayPluginConfigurationProperties configurationProperties;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    MerlinPropertiesFactory merlinPropertiesFactory;
    @Autowired
    SpringBus springBus;

    @Bean
    WsGatewayPluginWebServiceClient dcGatewayWebServiceClient() {
        return new WsGatewayPluginWebServiceClient();
    }

    @Bean
    DomibusConnectorGatewaySubmissionWebService gatewaySubmissionWebserviceProxy() {
        // JaxWsClientProxy jaxWsClientProxy = new JaxWsClientProxy();
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorGatewaySubmissionWebService.class);

        WsPolicyLoader wsPolicyLoader = new WsPolicyLoader(configurationProperties.getWsPolicy());
        jaxWsProxyFactoryBean.getFeatures().add(wsPolicyLoader.loadPolicyFeature());

        jaxWsProxyFactoryBean.setAddress(configurationProperties.getGwAddress());
        jaxWsProxyFactoryBean.setWsdlURL(DomibusConnectorGatewayWSService.WSDL_LOCATION.toString());
        jaxWsProxyFactoryBean.setServiceName(DomibusConnectorGatewayWSService.SERVICE);
        jaxWsProxyFactoryBean.setEndpointName(DomibusConnectorGatewayWSService.DomibusConnectorGatewayWebService);

        Map<String, Object> props = jaxWsProxyFactoryBean.getProperties();
        if (props == null) {
            props = new HashMap<>();
        }

        if (configurationProperties.isCxfLoggingEnabled()) {
            LoggingFeature loggingFeature = new LoggingFeature();
            loggingFeature.setPrettyLogging(true);
            jaxWsProxyFactoryBean.getFeatures().add(loggingFeature);
        }

        Properties properties = merlinPropertiesFactory.mapCertAndStoreConfigPropertiesToMerlinProperties(
                configurationProperties.getSoap(),
                "."
        );

        props.put("mtom-enabled", true);
        props.put("security.encryption.properties", properties);
        props.put("security.encryption.username", configurationProperties.getSoap().getEncryptAlias());
        props.put("security.signature.properties", properties);
        props.put("security.callback-handler", new DefaultWsCallbackHandler());

        jaxWsProxyFactoryBean.setProperties(props);

        return (DomibusConnectorGatewaySubmissionWebService) jaxWsProxyFactoryBean.create();
    }

    @Bean
    EndpointImpl gatewayDeliveryWebServiceEndpoint() {
        WsGatewayPluginConfigurationProperties config = configurationProperties;
        EndpointImpl endpoint = new EndpointImpl(springBus, wsGatewayPluginDeliveryServiceEndpoint());
        endpoint.setAddress(config.getGwDeliveryServicePublishAddress());
        endpoint.setServiceName(DomibusConnectorGatewayDeliveryWSService.SERVICE);
        endpoint.setEndpointName(DomibusConnectorGatewayDeliveryWSService.DomibusConnectorGatewayDeliveryWebService);
        endpoint.setWsdlLocation(DomibusConnectorGatewayDeliveryWSService.WSDL_LOCATION.toString());

        WSPolicyFeature wsPolicyFeature = new WsPolicyLoader(config.getWsPolicy()).loadPolicyFeature();
        endpoint.getFeatures().add(wsPolicyFeature);

        if (config.isCxfLoggingEnabled()) {
            LoggingFeature loggingFeature = new LoggingFeature();
            loggingFeature.setPrettyLogging(true);
            endpoint.getFeatures().add(loggingFeature);
        }

        endpoint.getProperties().put("security.callback-handler", new DefaultWsCallbackHandler());
        endpoint.getProperties().put("security.store.bytes.in.attachment", true);
        endpoint.getProperties().put("security.enable.streaming", true);
        endpoint.getProperties().put("mtom-enabled", true);

        Properties encSigProperties = merlinPropertiesFactory.mapCertAndStoreConfigPropertiesToMerlinProperties(
                configurationProperties.getSoap(),
                ""
        );

        endpoint.getProperties().put("security.encryption.properties", encSigProperties);
        endpoint.getProperties().put("security.signature.properties", encSigProperties);
        endpoint.getProperties().put("security.encryption.username", "useReqSigCert");

        endpoint.publish();
        LOGGER.debug("Published WebService [{}] under [{}]",
                     DomibusConnectorGatewayDeliveryWSService.class,
                     config.getGwDeliveryServicePublishAddress()
        );
        return endpoint;
    }

    @Bean
    WsGatewayPluginDeliveryServiceEndpointImpl wsGatewayPluginDeliveryServiceEndpoint() {
        return new WsGatewayPluginDeliveryServiceEndpointImpl();
    }
}
