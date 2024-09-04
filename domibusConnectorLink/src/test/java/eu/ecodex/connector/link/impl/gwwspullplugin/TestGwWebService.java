/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.impl.gwwspullplugin;

import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.ws.gateway.webservice.DomibusConnectorGatewayWebService;
import eu.ecodex.connector.ws.gateway.webservice.GetMessageByIdRequest;
import eu.ecodex.connector.ws.gateway.webservice.ListPendingMessageIdsResponse;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
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
import eu.ecodex.connector.link.util.GetServerAddress;

/**
 * IMPLEMENTATION OF THE GW WEB SERVICE INTERFACE FOR TESTING PURPOSE.
 */
@SpringBootApplication(
    scanBasePackageClasses = {TestGwWebService.class}, exclude = {DataSourceAutoConfiguration.class}
)
@ImportResource(
    "classpath:/test/eu/domibus/connector/link/gwwspullplugin/TestPullGatewayContext.xml"
)
@Profile("testgwpull")
public class TestGwWebService {
    /**
     * The ListPendingMessagesMock interface provides a method to list pending message IDs.
     */
    public interface ListPendingMessagesMock {
        ListPendingMessageIdsResponse listPendingMessageIds();
    }

    /**
     * This interface provides a method to retrieve a specific message by its ID.
     */
    public interface GetMessageByIdMock {
        DomibusConnectorMessageType getMessageById(GetMessageByIdRequest getMessageByIdRequest);
    }

    public static final String TEST_GW_PULL_PROFILE_NAME = "testgwpull";
    public static final String TO_PULL_GW_SUBMITTED_MESSAGES_BLOCKING_QUEUE_BEAN_NAME =
        "toPullGwSubmittedMessagesQueue";

    /**
     * Starts the context for the TestGwWebService class with the specified properties.
     *
     * @param properties the properties to configure the application context
     * @return the ConfigurableApplicationContext instance representing the started context
     */
    public static ConfigurableApplicationContext startContext(Map<String, Object> properties) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder.sources(TestGwWebService.class)
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
        return new LinkedBlockingQueue<>(20);
    }

    /**
     * Retrieves the server address.
     *
     * @return The server address in the format "http://localhost:{port}/services/pullservice".
     */
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

    /**
     * Creates a mock object for the ListPendingMessagesMock interface.
     *
     * @return A mock object of type ListPendingMessagesMock
     */
    @Bean
    @ConditionalOnMissingBean
    public ListPendingMessagesMock listPendingMessagesMock() {
        ListPendingMessagesMock mock = Mockito.mock(ListPendingMessagesMock.class);
        ListPendingMessageIdsResponse resp = new ListPendingMessageIdsResponse();
        Mockito.when(mock.listPendingMessageIds()).thenReturn(resp);
        return mock;
    }

    /**
     * Creates a mock object for the GetMessageByIdMock interface. This method is annotated with
     * Bean and @ConditionalOnMissingBean.
     *
     * @return A mock object of type GetMessageByIdMock
     */
    @Bean
    @ConditionalOnMissingBean
    public GetMessageByIdMock getMessageByIdMock() {
        return Mockito.mock(GetMessageByIdMock.class);
    }

    /**
     * Returns an instance of the DomibusConnectorGatewayWebService interface.
     *
     * @return An instance of DomibusConnectorGatewayWebService
     */
    @Bean("testGwPullService")
    public DomibusConnectorGatewayWebService domibusConnectorGatewayWebService() {
        return new TestGwPullServiceImpl();
    }
}
