package eu.domibus.connectorplugins.link.gwwspullplugin;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.link.common.DefaultWsCallbackHandler;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.gateway.webservice.DomibusConnectorGatewayWSService;
import eu.domibus.connector.ws.gateway.webservice.DomibusConnectorGatewayWebService;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static eu.domibus.connector.tools.logging.LoggingMarker.Log4jMarker.CONFIG;


/**
 * Configuration for the spring childContext for
 * the pullGatewayPlugin
 */
@Configuration
//@Profile(DCGatewayPullPluginConfiguration.DC_GATEWAY_PULL_PLUGIN_PROFILE)
@EnableConfigurationProperties(DCGatewayPullPluginConfigurationProperties.class)
@ComponentScan(basePackageClasses = DCGatewayPullPluginConfiguration.class)
public class DCGatewayPullPluginConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(DCGatewayPullPluginConfiguration.class);
    public static final String DC_GATEWAY_PULL_PLUGIN_PROFILE = "link.gwwspullplugin";

    @Autowired
    DCGatewayPullPluginConfigurationProperties configurationProperties;
    @Autowired
    ApplicationContext applicationContext;

    @Bean
    DCGatewayWebServiceClient dcGatewayWebServiceClient() {
        return new DCGatewayWebServiceClient();
    }

    @Bean
    DomibusConnectorGatewayWebService pullGwWebServiceProxy() {
        // JaxWsClientProxy jaxWsClientProxy = new JaxWsClientProxy();
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorGatewayWebService.class);

        WsPolicyLoader wsPolicyLoader = new WsPolicyLoader(configurationProperties.getWsPolicy());
        jaxWsProxyFactoryBean.getFeatures().add(wsPolicyLoader.loadPolicyFeature());
        if (loggingFeature() != null) {
            jaxWsProxyFactoryBean.getFeatures().add(loggingFeature());
        }

        jaxWsProxyFactoryBean.setAddress(configurationProperties.getGwAddress());
        jaxWsProxyFactoryBean.setWsdlURL(DomibusConnectorGatewayWSService.WSDL_LOCATION.toString());
        jaxWsProxyFactoryBean.setServiceName(DomibusConnectorGatewayWSService.SERVICE);
        jaxWsProxyFactoryBean.setEndpointName(DomibusConnectorGatewayWSService.DomibusConnectorGatewayWebService);

        Map<String, Object> props = jaxWsProxyFactoryBean.getProperties();
        if (props == null) {
            props = new HashMap<>();
        }

        props.put("mtom-enabled", true);
        props.put("security.encryption.properties", gwWsLinkEncryptProperties());
        props.put("security.encryption.username", configurationProperties.getSoap().getEncryptAlias());
        props.put("security.signature.properties", gwWsLinkEncryptProperties());
        props.put("security.callback-handler", new DefaultWsCallbackHandler());

        LOGGER.debug(CONFIG, "Creating pullGwWebServiceProxy with properties [{}]", props);
        jaxWsProxyFactoryBean.setProperties(props);

        return (DomibusConnectorGatewayWebService) jaxWsProxyFactoryBean.create();
    }

    private Feature loggingFeature() {
        if (configurationProperties.isCxfLoggingEnabled()) {
            LoggingFeature loggingFeature = new LoggingFeature();
            loggingFeature.setPrettyLogging(true);
            return loggingFeature;
        }
        return null;
    }

    public Properties gwWsLinkEncryptProperties() {
        Properties props = new Properties();
        // TODO: use MerlinPropertiesFactory
        CxfTrustKeyStoreConfigurationProperties cxf = configurationProperties.getSoap(); //.getCxf();
        StoreConfigurationProperties cxfKeyStore = cxf.getKeyStore();
        props.put("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        props.put("org.apache.wss4j.crypto.merlin.keystore.type", cxfKeyStore.getType());
        props.put("org.apache.wss4j.crypto.merlin.keystore.file", cxfKeyStore.getPath());
        props.put("org.apache.wss4j.crypto.merlin.keystore.password", cxfKeyStore.getPassword());
        props.put("org.apache.wss4j.crypto.merlin.keystore.alias", cxf.getPrivateKey().getAlias());
        props.put("org.apache.wss4j.crypto.merlin.keystore.private.password", cxf.getPrivateKey().getPassword());

        props.put("org.apache.wss4j.crypto.merlin.truststore.type", cxf.getTrustStore().getType());
        props.put("org.apache.wss4j.crypto.merlin.truststore.file", cxf.getTrustStore().getPath());
        props.put("org.apache.wss4j.crypto.merlin.truststore.password", cxf.getTrustStore().getPassword());

        LOGGER.debug(CONFIG, "Creating gwWsLinkEncryptProperties with properties [{}]", props);
        return props;
    }
}
