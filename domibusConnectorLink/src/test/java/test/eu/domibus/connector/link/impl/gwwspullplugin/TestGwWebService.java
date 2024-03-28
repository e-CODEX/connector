package test.eu.domibus.connector.link.impl.gwwspullplugin;


import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.gateway.webservice.DomibusConnectorGatewayWebService;
import eu.domibus.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.domibus.connector.ws.gateway.webservice.ListPendingMessageIdsResponse;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import test.eu.domibus.connector.link.util.GetServerAddress;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * IMPLEMENTATION OF THE GW WEB SERIVCE INTERFACE
 * FOR TESTING PURPOSE
 */
@SpringBootApplication(scanBasePackageClasses = {TestGwWebService.class}, exclude = {DataSourceAutoConfiguration.class})
@ImportResource("classpath:/test/eu/domibus/connector/link/gwwspullplugin/TestPullGatewayContext.xml")
@Profile("testgwpull")
public class TestGwWebService {
    public static final String TEST_GW_PULL_PROFILE_NAME = "testgwpull";
    public static final String TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME =
            "toPullGwSubmittedMessagesQueue";

    public static ConfigurableApplicationContext startContext(Map<String, Object> properties) {
        //        if (properties == null) {
        //            properties = new HashMap<>();
        //        }
        //        properties.put("server.port", SocketUtils.findAvailableTcpPort());

        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder
                .sources(TestGwWebService.class)
                .web(WebApplicationType.SERVLET)
                .properties(properties)
                .bannerMode(Banner.Mode.OFF)
                .profiles(TEST_GW_PULL_PROFILE_NAME)
                .build();
        return springApp.run();
    }

    @Bean(TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME)
    @Qualifier(TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME)
    public LinkedBlockingQueue<DomibusConnectorMessageType> submittedMessagesList() {
        return new LinkedBlockingQueue<DomibusConnectorMessageType>(20);
    }

    @Bean
    public GetServerAddress getServerAddress() {
        return new GetServerAddress() {

            @Value("${server.port}")
            int serverPort;

            @Override
            public String getServerAddress() {
                return "http://localhost:" + serverPort + "/services/pullservice";
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public ListPendingMessagesMock listPendingMessagesMock() {
        ListPendingMessagesMock mock = Mockito.mock(ListPendingMessagesMock.class);
        ListPendingMessageIdsResponse resp = new ListPendingMessageIdsResponse();
        Mockito.when(mock.listPendingMessageIds()).thenReturn(resp);
        return mock;
    }

    @Bean
    @ConditionalOnMissingBean
    public GetMessageByIdMock getMessageByIdMock() {
        return Mockito.mock(GetMessageByIdMock.class);
    }

    @Bean("testGwPullService")
    public DomibusConnectorGatewayWebService domibusConnectorGatewayWebService() {
        return new TestGwPullServiceImpl();
    }

    public interface ListPendingMessagesMock {
        ListPendingMessageIdsResponse listPendingMessageIds();
    }

    public interface GetMessageByIdMock {
        DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest);
    }
}
