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
import jakarta.jms.Message;
import jakarta.jms.Queue;
import java.util.List;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.junit.jupiter.api.Assertions;
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

@SuppressWarnings("squid:S1135")
@SpringBootTest(
    classes = {MoveMessagesFromDlqToQueueTest.MyTestContext.class},
    properties = {"spring.liquibase.enabled=false"}
)
@ActiveProfiles({"test", "jms-test"})
@DirtiesContext
@Disabled // TODO: spring context problems
class MoveMessagesFromDlqToQueueTest {
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

    @BeforeEach
    public void beforeEach() {
        nonXaJmsTemplate = new JmsTemplate(nonXaConnectionFactory);
        nonXaJmsTemplate.setMessageConverter(converter);
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

    @Test
    void it_is_possible_to_move_a_message_from_the_dlq_back_to_the_queue() {

        // Arrange
        final QueueHelper sut = new QueueHelper(q1, dlq1, nonXaJmsTemplate);
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("msg1"));

        final List<Message> test1 = sut.listAllMessagesInDlq(); // debug

        nonXaJmsTemplate.convertAndSend(dlq1, message);
        final List<Message> messages = sut.listAllMessagesInDlq();
        final Message jmsMessage = messages.get(0);

        // Act
        sut.moveMsgFromDlqToQueue(jmsMessage);
        final List<Message> listMsgDlq = sut.listAllMessagesInDlq();
        final DomibusConnectorMessage msgOnQueue =
            (DomibusConnectorMessage) nonXaJmsTemplate.receiveAndConvert(q1);
        final List<Message> listMsgDlq2 = sut.listAllMessagesInDlq();

        // Assert
        Assertions.assertAll(
            () -> assertThat(msgOnQueue.getConnectorMessageId().getConnectorMessageId()).isEqualTo(
                "msg1"),
            () -> assertThat(listMsgDlq).isEmpty()
        );
    }
}
