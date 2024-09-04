/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.queues;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.controller.processor.CleanupMessageProcessor;
import eu.ecodex.connector.controller.processor.EvidenceMessageProcessor;
import eu.ecodex.connector.controller.processor.ToBackendBusinessMessageProcessor;
import eu.ecodex.connector.controller.processor.ToGatewayBusinessMessageProcessor;
import eu.ecodex.connector.controller.queues.listener.ToLinkPartnerListener;
import eu.ecodex.connector.controller.queues.producer.ToCleanupQueue;
import eu.ecodex.connector.controller.queues.producer.ToConnectorQueue;
import eu.ecodex.connector.controller.queues.producer.ToLinkQueue;
import eu.ecodex.connector.controller.service.SubmitToConnector;
import eu.ecodex.connector.controller.service.SubmitToLinkService;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    classes = {DeleteMessagesFromQueuesTest.MyTestContext.class},
    properties = {"spring.liquibase.enabled=false"}
)
@ActiveProfiles({"test", "jms-test"})
@DirtiesContext
@Disabled // does not run on CI
class DeleteMessagesFromQueuesTest {
    @SpringBootApplication
    public static class MyTestContext {
        @Bean("TestQueue1")
        public Queue createTestQueue1() {
            return new ActiveMQQueue("q1");
        }

        @Bean("TestDlq1")
        public Queue createTestDlq1() {
            return new ActiveMQQueue("dlq1");
        }
    }

    public List<Message> receiveAllFromQueue(Destination destination) {
        return nonXaJmsTemplate.execute(session -> {
            try (final MessageConsumer consumer = session.createConsumer(destination)) {
                List<Message> messages = new ArrayList<>();
                Message message;
                while ((message = consumer.receiveNoWait()) != null) {
                    messages.add(message);
                }
                return messages;
            }
        }, true);
    }

    @Autowired
    @Qualifier("TestQueue1")
    Queue q1;
    @Autowired
    @Qualifier("TestDlq1")
    Queue dlq1;
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
    ToLinkPartnerListener toLinkPartnerListener;
    @MockBean
    CleanupMessageProcessor cleanupMessageProcessor;
    @Autowired
    ToLinkQueue toLinkQueueProducer;
    @Autowired
    ToConnectorQueue toConnectorQueueProducer;
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
    JmsTemplate nonXaJmsTemplate;
    @Autowired
    MessageConverter converter;

    @BeforeEach
    public void beforeEach() {
        nonXaJmsTemplate = new JmsTemplate(nonXaConnectionFactory);
        nonXaJmsTemplate.setMessageConverter(converter);
    }

    @Test
    void it_is_possible_to_delete_specific_msgs_from_queues_and_dlqs() throws JMSException {
        // Arrange
        final QueueHelper sut = new QueueHelper(q1, dlq1, nonXaJmsTemplate);
        DomibusConnectorMessage msgDlq = DomainEntityCreator.createMessage();
        msgDlq.setConnectorMessageId(new DomibusConnectorMessageId("msgDlq"));
        DomibusConnectorMessage msgQueue = DomainEntityCreator.createMessage();
        msgQueue.setConnectorMessageId(new DomibusConnectorMessageId("msgQueue"));
        nonXaJmsTemplate.convertAndSend(q1, msgQueue); // put something on the queue
        nonXaJmsTemplate.convertAndSend(dlq1, msgDlq); // put something on the dlq
        nonXaJmsTemplate.setReceiveTimeout(1000L);
        final List<Message> queue = sut.listAllMessages();
        final List<Message> dlq = sut.listAllMessagesInDlq();
        final Message jmsMsgQueue = queue.getFirst();
        final Message jmsMsgDlq = dlq.getFirst();

        // Assert Precondition
        assertThat(queue).isNotEmpty();
        assertThat(dlq).isNotEmpty();

        // Act
        sut.deleteMsg(jmsMsgQueue);
        sut.deleteMsg(jmsMsgDlq);
        sut.deleteMsg(
            // tests that code works if you delete a msg that is not there, should be in another
            // test, but no time
            jmsMsgDlq
        );

        // Assert
        assertThat(sut.listAllMessages()).isEmpty();
        assertThat(sut.listAllMessagesInDlq()).isEmpty();
    }
}
