/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.test.util;

import eu.ecodex.connector.controller.exception.DomibusConnectorControllerException;
import eu.ecodex.connector.controller.exception.DomibusConnectorGatewaySubmissionException;
import eu.ecodex.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.ecodex.connector.controller.service.SubmitToLinkService;
import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.ecodex.connector.domain.enums.MessageTargetSource;
import eu.ecodex.connector.domain.enums.TransportState;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.ecodex.connector.lib.logging.aspects.MDCSetterAspectConfiguration;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import eu.ecodex.connector.persistence.testutils.LargeFileProviderMemoryImpl;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This class represents the configuration for the test context in an IT case.
 * It specifies the base packages to scan for component classes, excludes
 * the {@link org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration}
 * class from auto-configuration, and enables transaction management.
 * It also defines beans for various services and interceptors used in the test context.
 */
@SpringBootApplication(scanBasePackages = {
    "eu.ecodex.connector.controller",  // load controller
    "eu.ecodex.connector.dss",         // load dss
    "eu.ecodex.connector.lib",         // load lib
    "eu.ecodex.connector.common",      // load common
    "eu.ecodex.connector.persistence", // load persistence
    "eu.ecodex.connector.evidences",   // load evidences toolkit
    "eu.ecodex.connector.security"     // load security toolkit
},
    excludeName = {
        "org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration"}
)
@EnableTransactionManagement
@Import(MDCSetterAspectConfiguration.class)
@Profile("ITCaseTestContext")
public class ITCaseTestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITCaseTestContext.class);
    // public static final java.lang.String TO_GW_DELIVERD_MESSAGES_LIST_BEAN_NAME
    // = "togwdeliveredmessages";
    // public static final java.lang.String TO_BACKEND_DELIVERD_MESSAGES_LIST_BEAN_NAME
    // = "tobackenddeliveredmessages";
    @Autowired
    DCMessagePersistenceService messagePersistenceService;

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    LargeFileProviderMemoryImpl largeFileProviderMemoryImpl() {
        return new LargeFileProviderMemoryImpl();
    }

    @Bean
    public DomibusConnectorGatewaySubmissionServiceInterceptor
    domibusConnectorGatewaySubmissionServiceInterceptor() {
        return Mockito.mock(DomibusConnectorGatewaySubmissionServiceInterceptor.class);
    }

    @Bean
    public DomibusConnectorBackendDeliveryServiceInterceptor
    domibusConnectorBackendDeliveryServiceInterceptor() {
        return Mockito.mock(DomibusConnectorBackendDeliveryServiceInterceptor.class);
    }

    @Bean
    public QueueBasedDomibusConnectorBackendDeliveryService
    domibusConnectorBackendDeliveryService() {
        return new QueueBasedDomibusConnectorBackendDeliveryService();
    }

    @Bean
    public QueueBasedDomibusConnectorGatewaySubmissionService sendMessageToGwService() {
        return new QueueBasedDomibusConnectorGatewaySubmissionService();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public SubmitToLinkService submitToLinkService() {
        return new QueueBasedSubmitToLinkService();
    }

    /**
     * Use this interface to tamper with the test...
     */
    public interface DomibusConnectorGatewaySubmissionServiceInterceptor {
        void submitToGateway(DomibusConnectorMessage message)
            throws DomibusConnectorGatewaySubmissionException;
    }

    /**
     * Use this interface to tamper with the test...
     */
    public interface DomibusConnectorBackendDeliveryServiceInterceptor {
        void deliveryToBackend(DomibusConnectorMessage message);
    }

    /**
     * Implementation of the SubmitToLinkService interface that uses a queue-based delivery
     * approach.
     * Messages are submitted to either a backend or a gateway based on the target specified
     * in the message details.
     */
    public static class QueueBasedSubmitToLinkService implements SubmitToLinkService {
        @Autowired
        QueueBasedDomibusConnectorBackendDeliveryService
            queueBasedDomibusConnectorBackendDeliveryService;
        @Autowired
        QueueBasedDomibusConnectorGatewaySubmissionService
            queueBasedDomibusConnectorGatewaySubmissionService;

        @Override
        public void submitToLink(DomibusConnectorMessage message)
            throws DomibusConnectorSubmitToLinkException {
            if (message.getConnectorMessageId() == null) {
                throw new IllegalArgumentException("connectorMessageId is null!");
            }
            try {
                MessageTargetSource target = message.getMessageDetails().getDirection().getTarget();
                if (target == MessageTargetSource.GATEWAY) {
                    queueBasedDomibusConnectorGatewaySubmissionService.submitToGateway(message);
                } else if (target == MessageTargetSource.BACKEND) {
                    queueBasedDomibusConnectorBackendDeliveryService.deliverMessageToBackend(
                        message);
                }
            } catch (Exception e) {
                throw new DomibusConnectorSubmitToLinkException(
                    message, DomibusConnectorRejectionReason.OTHER, e);
            }
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocType")
    public static class QueueBasedDomibusConnectorBackendDeliveryService {
        // @Autowired
        // @Qualifier(TO_BACKEND_DELIVERD_MESSAGES_LIST_BEAN_NAME)
        public BlockingQueue<DomibusConnectorMessage> toBackendDeliveredMessages =
            new ArrayBlockingQueue<>(100);
        @Autowired
        TransportStateService transportStateService;
        @Autowired
        DomibusConnectorBackendDeliveryServiceInterceptor interceptor;

        /**
         * Delivers the given {@link DomibusConnectorMessage} to the backend.
         *
         * @param message The message to be delivered to the backend
         * @throws DomibusConnectorControllerException If an error occurs while delivering
         *                                             the message to the backend
         */
        public synchronized void deliverMessageToBackend(DomibusConnectorMessage message)
            throws DomibusConnectorControllerException {
            interceptor.deliveryToBackend(message);

            LOGGER.info("Delivered Message [{}] to Backend", message);

            TransportStateService.TransportId transportId =
                transportStateService.createTransportFor(
                    message,
                    new DomibusConnectorLinkPartner.LinkPartnerName(
                        "dummy_backend")
                );
            TransportStateService.DomibusConnectorTransportState state =
                new TransportStateService.DomibusConnectorTransportState();
            state.setConnectorTransportId(transportId);
            state.setStatus(TransportState.ACCEPTED);

            java.lang.String backendMsgId = "BACKEND_" + UUID.randomUUID();
            state.setRemoteMessageId(backendMsgId); // assigned backend message id
            state.setTransportImplId("mem_" + UUID.randomUUID()); // set a transport id
            transportStateService.updateTransportToBackendClientStatus(transportId, state);

            DomibusConnectorMessage msg = DomibusConnectorMessageBuilder.createBuilder()
                .copyPropertiesFrom(message)
                .build();
            msg.getMessageDetails().setBackendMessageId(backendMsgId);

            toBackendDeliveredMessages.add(msg);
        }

        public synchronized void clearQueue() {
            toBackendDeliveredMessages = new ArrayBlockingQueue<>(100);
        }

        public synchronized BlockingQueue<DomibusConnectorMessage> getQueue() {
            return this.toBackendDeliveredMessages;
        }
    }

    /**
     * This class provides a queue-based implementation of the
     * DomibusConnectorGatewaySubmissionService interface.
     * It allows submitting messages to the gateway and maintains a blocking queue to store
     * the submitted messages.
     */
    public static class QueueBasedDomibusConnectorGatewaySubmissionService {
        // @Autowired
        // @Qualifier(TO_GW_DELIVERD_MESSAGES_LIST_BEAN_NAME)
        public BlockingQueue<DomibusConnectorMessage> toGatewayDeliveredMessages =
            new ArrayBlockingQueue<>(100);
        @Autowired
        TransportStateService transportStateService;
        @Autowired
        DomibusConnectorGatewaySubmissionServiceInterceptor interceptor;

        /**
         * Submits a message to the gateway for further processing.
         *
         * @param message The message to be submitted to the gateway.
         * @throws DomibusConnectorGatewaySubmissionException If an error occurs during the
         *                                                    submission process.
         */
        public synchronized void submitToGateway(DomibusConnectorMessage message)
            throws DomibusConnectorGatewaySubmissionException {
            interceptor.submitToGateway(message);
            LOGGER.info("Delivered Message [{}] to Gateway", message);

            TransportStateService.TransportId dummyGW =
                transportStateService.createTransportFor(
                    message,
                    new DomibusConnectorLinkPartner.LinkPartnerName(
                        "dummy_gw")
                );
            TransportStateService.DomibusConnectorTransportState state =
                new TransportStateService.DomibusConnectorTransportState();
            state.setConnectorTransportId(dummyGW);
            // state.setConnectorMessageId(new DomibusConnectorMessage
            // .DomibusConnectorMessageId(message.getConnectorMessageId()));
            state.setStatus(TransportState.ACCEPTED);
            java.lang.String ebmsId = "EBMS_" + UUID.randomUUID();
            state.setRemoteMessageId(ebmsId); // assigned EBMS ID
            state.setTransportImplId("mem_" + UUID.randomUUID()); // set a transport id
            transportStateService.updateTransportToGatewayStatus(dummyGW, state);

            DomibusConnectorMessage msg = DomibusConnectorMessageBuilder.createBuilder()
                .copyPropertiesFrom(message)
                .build();
            msg.getMessageDetails().setEbmsMessageId(ebmsId);

            toGatewayDeliveredMessages.add(msg);
        }

        public synchronized void clearQueue() {
            toGatewayDeliveredMessages = new ArrayBlockingQueue<>(100);
        }

        public synchronized BlockingQueue<DomibusConnectorMessage> getQueue() {
            return this.toGatewayDeliveredMessages;
        }
    }
}
