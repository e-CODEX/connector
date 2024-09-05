/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.wsbackendplugin;

import static eu.ecodex.connector.link.wsbackendplugin.ConnectorClientTestBackend.PUSH_BACKEND_PROFILE_NAME;

import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.link.util.GetServerAddress;
import eu.ecodex.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.ecodex.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.namespace.QName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Configuration class for the Backend Client Push WebService.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@Profile(PUSH_BACKEND_PROFILE_NAME)
@ImportResource(
    {"classpath:/eu/ecodex/connector/link/wsbackendplugin/testclient_pushdelivery.xml"}
)
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

    /**
     * Retrieves the server address.
     *
     * @return the server address as a string.
     */
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
