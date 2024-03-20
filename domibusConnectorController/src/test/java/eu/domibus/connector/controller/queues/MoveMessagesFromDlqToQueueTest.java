package eu.domibus.connector.controller.queues;

import eu.domibus.connector.controller.processor.CleanupMessageProcessor;
import eu.domibus.connector.controller.processor.EvidenceMessageProcessor;
import eu.domibus.connector.controller.processor.ToBackendBusinessMessageProcessor;
import eu.domibus.connector.controller.processor.ToGatewayBusinessMessageProcessor;
import eu.domibus.connector.controller.queues.listener.ToLinkPartnerListener;
import eu.domibus.connector.controller.queues.producer.ToCleanupQueue;
import eu.domibus.connector.controller.queues.producer.ToConnectorQueue;
import eu.domibus.connector.controller.queues.producer.ToLinkQueue;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
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

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {MoveMessagesFromDlqToQueueTest.MyTestContext.class}, properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles({"test", "jms-test"})
@DirtiesContext
@Disabled //TODO: spring context problems
public class MoveMessagesFromDlqToQueueTest {

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
    public void it_is_possible_to_move_a_message_from_the_dlq_back_to_the_queue() throws JMSException {

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
        final DomibusConnectorMessage msgOnQueue = (DomibusConnectorMessage) nonXaJmsTemplate.receiveAndConvert(q1);
        final List<Message> listMsgDlq2 = sut.listAllMessagesInDlq();

        // Assert
        Assertions.assertAll(
                () -> assertThat(msgOnQueue.getConnectorMessageId().getConnectorMessageId()).isEqualTo("msg1"),
                () -> assertThat(listMsgDlq).isEmpty()
        );
    }
}
