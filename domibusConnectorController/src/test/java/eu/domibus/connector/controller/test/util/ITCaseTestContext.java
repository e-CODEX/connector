package eu.domibus.connector.controller.test.util;

import eu.domibus.connector.controller.exception.DomibusConnectorControllerException;
import eu.domibus.connector.controller.exception.DomibusConnectorGatewaySubmissionException;
import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.lib.logging.aspects.MDCSetterAspectConfiguration;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.testutils.LargeFileProviderMemoryImpl;
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

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@SpringBootApplication(
        scanBasePackages = {
                "eu.domibus.connector.controller",  // load controller
                "eu.domibus.connector.dss",         // load dss
                "eu.domibus.connector.lib",         // load lib
                "eu.domibus.connector.common",      // load common
                "eu.domibus.connector.persistence", // load persistence
                "eu.domibus.connector.evidences",   // load evidences toolkit
                "eu.domibus.connector.security"     // load security toolkit
        },
        excludeName = {"org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration"}
)
@EnableTransactionManagement
@Import(MDCSetterAspectConfiguration.class)
@Profile("ITCaseTestContext")
public class ITCaseTestContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(ITCaseTestContext.class);

    // public static final java.lang.String TO_GW_DELIVERD_MESSAGES_LIST_BEAN_NAME = "togwdeliveredmessages";
    // public static final java.lang.String TO_BACKEND_DELIVERD_MESSAGES_LIST_BEAN_NAME =
    // "tobackenddeliveredmessages";
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
    public DomibusConnectorGatewaySubmissionServiceInterceptor domibusConnectorGatewaySubmissionServiceInterceptor() {
        return Mockito.mock(DomibusConnectorGatewaySubmissionServiceInterceptor.class);
    }

    @Bean
    public DomibusConnectorBackendDeliveryServiceInterceptor domibusConnectorBackendDeliveryServiceInterceptor() {
        return Mockito.mock(DomibusConnectorBackendDeliveryServiceInterceptor.class);
    }

    @Bean
    public QueueBasedDomibusConnectorBackendDeliveryService domibusConnectorBackendDeliveryService() {
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
        void submitToGateway(DomibusConnectorMessage message) throws DomibusConnectorGatewaySubmissionException;
    }

    /**
     * Use this interface to tamper with the test...
     */
    public interface DomibusConnectorBackendDeliveryServiceInterceptor {
        void deliveryToBackend(DomibusConnectorMessage message);
    }

    public static class QueueBasedSubmitToLinkService implements SubmitToLinkService {
        @Autowired
        QueueBasedDomibusConnectorBackendDeliveryService queueBasedDomibusConnectorBackendDeliveryService;
        @Autowired
        QueueBasedDomibusConnectorGatewaySubmissionService queueBasedDomibusConnectorGatewaySubmissionService;

        @Override
        public void submitToLink(DomibusConnectorMessage message) throws DomibusConnectorSubmitToLinkException {
            if (message.getConnectorMessageId() == null) {
                throw new IllegalArgumentException("connectorMessageId is null!");
            }
            try {
                MessageTargetSource target = message.getMessageDetails().getDirection().getTarget();
                if (target == MessageTargetSource.GATEWAY) {
                    queueBasedDomibusConnectorGatewaySubmissionService.submitToGateway(message);
                } else if (target == MessageTargetSource.BACKEND) {
                    queueBasedDomibusConnectorBackendDeliveryService.deliverMessageToBackend(message);
                }
            } catch (Exception e) {
                throw new DomibusConnectorSubmitToLinkException(message, DomibusConnectorRejectionReason.OTHER, e);
            }
        }
    }

    public static class QueueBasedDomibusConnectorBackendDeliveryService {

        // @Autowired
        // @Qualifier(TO_BACKEND_DELIVERD_MESSAGES_LIST_BEAN_NAME)
        public BlockingQueue<DomibusConnectorMessage> toBackendDeliveredMessages = new ArrayBlockingQueue<>(100);
        @Autowired
        TransportStateService transportStateService;
        @Autowired
        DomibusConnectorBackendDeliveryServiceInterceptor interceptor;

        public synchronized void deliverMessageToBackend(DomibusConnectorMessage message) throws DomibusConnectorControllerException {
            interceptor.deliveryToBackend(message);

            LOGGER.info("Delivered Message [{}] to Backend", message);

            TransportStateService.TransportId transportId = transportStateService.createTransportFor(
                    message,
                    new DomibusConnectorLinkPartner.LinkPartnerName("dummy_backend")
            );
            TransportStateService.DomibusConnectorTransportState state =
                    new TransportStateService.DomibusConnectorTransportState();
            state.setConnectorTransportId(transportId);
            state.setStatus(TransportState.ACCEPTED);

            java.lang.String backendMsgId = "BACKEND_" + UUID.randomUUID();
            state.setRemoteMessageId(backendMsgId); // assigned backend message id
            state.setTransportImplId("mem_" + UUID.randomUUID()); // set a transport id
            transportStateService.updateTransportToBackendClientStatus(transportId, state);

            DomibusConnectorMessage msg = DomibusConnectorMessageBuilder
                    .createBuilder()
                    .copyPropertiesFrom(message)
                    .build();
            msg.getMessageDetails().setBackendMessageId(backendMsgId);

            toBackendDeliveredMessages.add(msg);
        }

        synchronized public void clearQueue() {
            toBackendDeliveredMessages = new ArrayBlockingQueue<>(100);
        }

        public synchronized BlockingQueue<DomibusConnectorMessage> getQueue() {
            return this.toBackendDeliveredMessages;
        }
    }

    public static class QueueBasedDomibusConnectorGatewaySubmissionService {
        // @Autowired
        // @Qualifier(TO_GW_DELIVERD_MESSAGES_LIST_BEAN_NAME)
        public BlockingQueue<DomibusConnectorMessage> toGatewayDeliveredMessages = new ArrayBlockingQueue<>(100);
        @Autowired
        TransportStateService transportStateService;
        @Autowired
        DomibusConnectorGatewaySubmissionServiceInterceptor interceptor;

        public synchronized void submitToGateway(DomibusConnectorMessage message) throws DomibusConnectorGatewaySubmissionException {
            interceptor.submitToGateway(message);
            LOGGER.info("Delivered Message [{}] to Gateway", message);

            TransportStateService.TransportId dummyGW = transportStateService.createTransportFor(
                    message,
                    new DomibusConnectorLinkPartner.LinkPartnerName("dummy_gw")
            );
            TransportStateService.DomibusConnectorTransportState state =
                    new TransportStateService.DomibusConnectorTransportState();
            state.setConnectorTransportId(dummyGW);
            // state.setConnectorMessageId(new DomibusConnectorMessage.DomibusConnectorMessageId(message
            // .getConnectorMessageId()));
            state.setStatus(TransportState.ACCEPTED);
            java.lang.String ebmsId = "EBMS_" + UUID.randomUUID();
            state.setRemoteMessageId(ebmsId); // assigned EBMS ID
            state.setTransportImplId("mem_" + UUID.randomUUID()); // set a transport id
            transportStateService.updateTransportToGatewayStatus(dummyGW, state);

            DomibusConnectorMessage msg = DomibusConnectorMessageBuilder
                    .createBuilder()
                    .copyPropertiesFrom(message)
                    .build();
            msg.getMessageDetails().setEbmsMessageId(ebmsId);

            toGatewayDeliveredMessages.add(msg);
        }

        synchronized public void clearQueue() {
            toGatewayDeliveredMessages = new ArrayBlockingQueue<>(100);
        }

        public synchronized BlockingQueue<DomibusConnectorMessage> getQueue() {
            return this.toGatewayDeliveredMessages;
        }
    }
}
