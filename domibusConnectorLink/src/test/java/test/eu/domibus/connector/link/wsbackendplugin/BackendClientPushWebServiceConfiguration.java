package test.eu.domibus.connector.link.wsbackendplugin;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import test.eu.domibus.connector.link.util.GetServerAddress;

import javax.xml.namespace.QName;
import java.util.concurrent.LinkedBlockingQueue;

import static test.eu.domibus.connector.link.wsbackendplugin.ConnectorClientTestBackend.PUSH_BACKEND_PROFILE_NAME;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@Profile(PUSH_BACKEND_PROFILE_NAME)
@ImportResource({"classpath:/test/eu/domibus/connector/link/wsbackendplugin/testclient_pushdelivery.xml"})
public class BackendClientPushWebServiceConfiguration {
    public static final String PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME = "deliveredMessagesListBean";

    @Bean
    @ConditionalOnMissingBean
    public static PropertySourcesPlaceholderConfigurer
    propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME)
    public LinkedBlockingQueue<DomibusConnectorMessageType> domibusConnectorMessageTypeList() {
        return new LinkedBlockingQueue<>();
    }

    @Bean("backendDeliveryWebService")
    public DomibusConnectorBackendDeliveryWebService domibusConnectorBackendDeliveryWebService() {
        return new DummyDomibusConnectorBackendDeliveryWebServiceImpl();
    }

    @Bean("backendDeliveryWebServiceName")
    public QName serviceName() {
        return DomibusConnectorBackendDeliveryWSService.DomibusConnectorBackendDeliveryWebService;
    }

    @Bean
    public GetServerAddress getServerAddress() {
        return new GetServerAddress() {

            @Value("${server.port}")
            int serverPort;

            @Override
            public String getServerAddress() {
                return "http://localhost:" + serverPort + "/services/backendDelivery";
            }
        };
    }
}
