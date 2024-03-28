package eu.domibus.connectorplugins.link.wsbackendplugin.childctx;

import eu.domibus.connector.link.common.MerlinPropertiesFactory;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import eu.domibus.connectorplugins.link.wsbackendplugin.WsBackendPluginActiveLinkPartner;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Properties;

import static eu.domibus.connector.tools.logging.LoggingMarker.Log4jMarker.CONFIG;


/**
 * Creates a web service client for pushing messages to backend client
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Component
public class WsBackendPluginWebServiceClientFactory {
    private static final Logger LOGGER = LogManager.getLogger(WsBackendPluginWebServiceClientFactory.class);

    private final WsBackendPluginConfigurationProperties config;
    private final MerlinPropertiesFactory merlinPropertiesFactory;

    public WsBackendPluginWebServiceClientFactory(
            WsBackendPluginConfigurationProperties config, MerlinPropertiesFactory merlinPropertiesFactory) {
        this.config = config;
        this.merlinPropertiesFactory = merlinPropertiesFactory;
    }

    public DomibusConnectorBackendDeliveryWebService createBackendWsClient(WsBackendPluginActiveLinkPartner linkPartner) {
        LOGGER.debug(CONFIG, "#createWsClient: creating WS endpoint for backendClient [{}]", linkPartner);
        WsBackendPluginLinkPartnerConfigurationProperties linkPartnerConfig = linkPartner.getConfig();
        String pushAddress = linkPartnerConfig.getPushAddress();
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorBackendDeliveryWebService.class);

        if (config.isCxfLoggingEnabled()) {
            jaxWsProxyFactoryBean.getFeatures().add(new LoggingFeature());
        }

        WSPolicyFeature wsPolicyFeature = new WsPolicyLoader(config.getWsPolicy()).loadPolicyFeature();
        jaxWsProxyFactoryBean.getFeatures().add(wsPolicyFeature);
        jaxWsProxyFactoryBean.setAddress(pushAddress);
        jaxWsProxyFactoryBean.setWsdlURL(DomibusConnectorBackendDeliveryWSService.WSDL_LOCATION.toString());
        // jaxWsProxyFactoryBean.setWsdlURL(pushAddress + "?wsdl"); //maybe load own wsdl instead of remote one?

        Properties properties =
                merlinPropertiesFactory.mapCertAndStoreConfigPropertiesToMerlinProperties(this.config.getSoap(), ".");

        HashMap<String, Object> jaxWsFactoryBeanProperties = new HashMap<>();
        jaxWsFactoryBeanProperties.put("security.encryption.username", linkPartnerConfig.getEncryptionAlias());
        jaxWsFactoryBeanProperties.put("mtom-enabled", true);
        jaxWsFactoryBeanProperties.put("security.encryption.properties", properties);
        jaxWsFactoryBeanProperties.put("security.signature.properties", properties);

        LOGGER.debug(
                CONFIG,
                "#createWsClient: Configuring WsClient with following properties: [{}]",
                jaxWsFactoryBeanProperties
        );

        jaxWsProxyFactoryBean.setProperties(jaxWsFactoryBeanProperties);

        return (DomibusConnectorBackendDeliveryWebService) jaxWsProxyFactoryBean.create();
    }
}
