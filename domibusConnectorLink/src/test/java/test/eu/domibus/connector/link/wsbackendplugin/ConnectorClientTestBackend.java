package test.eu.domibus.connector.link.wsbackendplugin;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.util.SocketUtils;
import test.eu.domibus.connector.link.util.GetServerAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static test.eu.domibus.connector.link.wsbackendplugin.BackendClientPushWebServiceConfiguration.PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME;


/**
 * A very simple connector backend for testing purposes
 *
 * messages can be pushed to and are stored within the Bean
 *
 */
@SpringBootApplication(scanBasePackageClasses = {ConnectorClientTestBackend.class},
        exclude = {DataSourceAutoConfiguration.class,
                ActiveMQAutoConfiguration.class,
                QuartzAutoConfiguration.class})
@Profile(ConnectorClientTestBackend.TEST_BACKEND_PROFILE_NAME)
@ImportResource("classpath:/test/eu/domibus/connector/link/wsbackendplugin/testclient.xml")
public class ConnectorClientTestBackend {

    public static final String TEST_BACKEND_PROFILE_NAME = "wsbackendprofile";
    public static final String PUSH_BACKEND_PROFILE_NAME = "ws-backendclient-server";


    //client alice...
    public static void main(String[] args) {
        Map<String, Object> props = new HashMap<>();
        startContext("alice", "http://localhost:8021/services/backend", SocketUtils.findAvailableTcpPort());
    }

    public static ConnectorClientTestBackend startContext(String clientName, String connectorAddress, int serverPort) {
        boolean pushClient = serverPort > 0;
        Map<String, Object> props = new HashMap<>();
        props.put("ws.backendclient.name", clientName);
        props.put("ws.backendclient.connector-address", connectorAddress);
        props.put("spring.main.allow-bean-definition-overriding", true);
        props.put("server.port", serverPort);
        List<String> profiles = new ArrayList<>();
        profiles.add(TEST_BACKEND_PROFILE_NAME);
        if (pushClient) {
            profiles.add(PUSH_BACKEND_PROFILE_NAME);
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplicationBuilder springApp = builder.sources(ConnectorClientTestBackend.class);
        if (pushClient) {
                springApp.web(WebApplicationType.SERVLET);
        } else {
            springApp.web(WebApplicationType.NONE);
        }
        springApp.profiles(profiles.toArray(new String[]{}))
                .properties(props)
                .bannerMode(Banner.Mode.OFF);

        ConfigurableApplicationContext configurableApplicationContext = springApp.build().run();
        return configurableApplicationContext.getBean(ConnectorClientTestBackend.class);
    }

    @Autowired
    ConfigurableApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * returns a LinkedBlockingQueue holding all messages
     * which have been pushed to the Pull backend
     * Will only work if it is a pull client
     * @return LinkedBlockingQueue holding all messages
     */
    public LinkedBlockingQueue<DomibusConnectorMessageType> submittedMessages() {
        return applicationContext.getBean(PUSH_DELIVERED_MESSAGES_LIST_BEAN_NAME, LinkedBlockingQueue.class);
    }

    /**
     * throws exception if not started as push backend!
     * @return the http address of the cxf pull backend webservice
     */
    public GetServerAddress getServerAddress() {
        return applicationContext.getBean(GetServerAddress.class);
    }

    /**
     * returns the created ConnectorClient CXF-Proxy
     * can be used to send messages to the connector backend for testing
     * @return ConnectorClient CXF-Proxy
     */
    public DomibusConnectorBackendWebService backendConnectorClientProxy() {
        return applicationContext.getBean(DomibusConnectorBackendWebService.class);
    }


    public void shutdown() {
        applicationContext.close();
    }

}
