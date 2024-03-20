package eu.domibus.connector.controller.queues;


import eu.domibus.connector.controller.processor.CleanupMessageProcessor;
import eu.domibus.connector.controller.processor.EvidenceMessageProcessor;
import eu.domibus.connector.controller.processor.ToBackendBusinessMessageProcessor;
import eu.domibus.connector.controller.processor.ToGatewayBusinessMessageProcessor;
import eu.domibus.connector.controller.queues.producer.ToCleanupQueue;
import eu.domibus.connector.controller.queues.producer.ToConnectorQueue;
import eu.domibus.connector.controller.queues.producer.ToLinkQueue;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.ConnectionFactory;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {DeadLetterQueueTest.MyTestContext.class}, properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles({"test", "jms-test"})
@Disabled
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class DeadLetterQueueTest {

    @SpringBootApplication
    public static class MyTestContext {
    }

    @BeforeEach
    public void beforeEach() {
        nonXaJmsTemplate = new JmsTemplate(nonXaConnectionFactory);
        nonXaJmsTemplate.setMessageConverter(converter);
        Mockito.reset(submitToLinkService);
    }

    @MockBean
    SubmitToLinkService submitToLinkService;

    @MockBean
    SubmitToConnector submitToConnector;

    @MockBean
    EvidenceMessageProcessor evidenceMessageProcessor;

    @MockBean
    ToBackendBusinessMessageProcessor toBackendBusinessMessageProcessor;

    @MockBean
    ToGatewayBusinessMessageProcessor toGatewayBusinessMessageProcessor;

    @MockBean
    CleanupMessageProcessor cleanupMessageProcessor;

    @Autowired
    ToLinkQueue toLinkQueueProducer;

    @Autowired
    ToConnectorQueue toConnectorQueueProducer;

    @Autowired
    JmsTemplate nonXaJmsTemplate;

    @Autowired
    ToCleanupQueue toCleanupQueueProducer;

    @Autowired
    QueuesConfigurationProperties queuesConfigurationProperties;

    @Autowired
    TransactionTemplate txTemplate;

    // Inject the primary (XA aware) ConnectionFactory
    @Autowired
    private ConnectionFactory defaultConnectionFactory;

    // Inject the XA aware ConnectionFactory (uses the alias and injects the same as above)
    @Autowired
    @Qualifier("xaJmsConnectionFactory")
    private ConnectionFactory xaConnectionFactory;

    // Inject the non-XA aware ConnectionFactory
    @Autowired
    @Qualifier("nonXaJmsConnectionFactory")
    private ConnectionFactory nonXaConnectionFactory;

    @Autowired
    MessageConverter converter;

    @Test
    public void when_message_handling_of_clean_up_queue_messages_fails_the_message_should_end_up_in_the_dead_letter_queue_configured_for_that_queue() {

        // Arrange
        Mockito.doThrow(new RuntimeException("FAIL MESSAGE")).when(cleanupMessageProcessor).processMessage(any());

        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("asdfasdfasdf"));

        final DomibusConnectorMessage[] domibusConnectorMessage = new DomibusConnectorMessage[1];

        // Act
        txTemplate.executeWithoutResult(tx -> toCleanupQueueProducer.putOnQueue(message));

//        This can be used in conjunction with jconsole to find whether queues have been created and contain messages
//        Thread.sleep(1000000000L);

        // Assert
        Assertions.assertAll("Should return Message from DLQ",
                () -> Assertions.assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
                    nonXaJmsTemplate.setReceiveTimeout(20000);
                    nonXaJmsTemplate.setSessionTransacted(false);
                    domibusConnectorMessage[0] = (DomibusConnectorMessage) nonXaJmsTemplate.receiveAndConvert(queuesConfigurationProperties.getCleanupDeadLetterQueue());
                }),
                () -> assertThat(domibusConnectorMessage[0])
                        .isNotNull()
                        .extracting(c -> c.getConnectorMessageId().getConnectorMessageId()).isEqualTo("asdfasdfasdf")
        );
    }

    @Test
    public void when_message_handling_in_transition_from_gateway_to_backend_fails_the_message_should_end_up_in_the_dead_letter_queue_configured_for_connector_to_link_queue() throws InterruptedException {

        // Arrange
        Mockito.doThrow(new RuntimeException("FAIL MESSAGE")).when(submitToLinkService).submitToLink(any());

        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("qwerqwerqwrerttz"));

        final DomibusConnectorMessage[] domibusConnectorMessage = new DomibusConnectorMessage[2];

        // Act
        txTemplate.executeWithoutResult(tx -> toLinkQueueProducer.putOnQueue(message));

//        Thread.sleep(300000000);

        // Assert
        Assertions.assertAll("Should return Message from DLQ",
                () -> Assertions.assertTimeoutPreemptively(Duration.ofSeconds(40), () -> {
                    nonXaJmsTemplate.setReceiveTimeout(20000);
//                    nonXaJmsTemplate.setSessionTransacted(false);
                    domibusConnectorMessage[0] = (DomibusConnectorMessage) nonXaJmsTemplate.receiveAndConvert(queuesConfigurationProperties.getToLinkDeadLetterQueue());
                    domibusConnectorMessage[1] = (DomibusConnectorMessage) nonXaJmsTemplate.receiveAndConvert(queuesConfigurationProperties.getToLinkQueue());
                }),
                () -> assertThat(domibusConnectorMessage[0])
                        .isNotNull()
                        .extracting(c -> c.getConnectorMessageId().getConnectorMessageId()).isEqualTo("qwerqwerqwrerttz"),
                () -> assertThat(domibusConnectorMessage[1])
                        .isNull()
        );
    }

    @Test
    public void when_message_handling_in_transition_from_gateway_to_connector_fails_the_message_should_end_up_in_the_connector_dead_letter_queue() {

        // Arrange
        Mockito.doThrow(new RuntimeException("FAIL MESSAGE")).when(evidenceMessageProcessor).processMessage(any());
        Mockito.doThrow(new RuntimeException("FAIL MESSAGE")).when(toGatewayBusinessMessageProcessor).processMessage(any());
        Mockito.doThrow(new RuntimeException("FAIL MESSAGE")).when(toBackendBusinessMessageProcessor).processMessage(any());

        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("yxcvyxcvyxcv"));

        final DomibusConnectorMessage[] domibusConnectorMessage = new DomibusConnectorMessage[1];

        // Act
        txTemplate.executeWithoutResult(tx -> toConnectorQueueProducer.putOnQueue(message));

        // Assert
        Assertions.assertAll("Should return Message from DLQ",
                () -> Assertions.assertTimeoutPreemptively(Duration.ofSeconds(40), () -> {
                    nonXaJmsTemplate.setReceiveTimeout(20000);
                    nonXaJmsTemplate.setSessionTransacted(false);
                    domibusConnectorMessage[0] = (DomibusConnectorMessage) nonXaJmsTemplate.receiveAndConvert(queuesConfigurationProperties.getToConnectorControllerDeadLetterQueue());
                }),
                () -> assertThat(domibusConnectorMessage[0])
                        .isNotNull()
                        .extracting(c -> c.getConnectorMessageId().getConnectorMessageId()).isEqualTo("yxcvyxcvyxcv")
        );
    }
}



