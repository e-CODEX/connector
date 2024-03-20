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

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {DeleteMessagesFromQueuesTest.MyTestContext.class}, properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles({"test", "jms-test"})
@DirtiesContext
@Disabled // does not run on CI
public class DeleteMessagesFromQueuesTest {

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
    public void it_is_possible_to_delete_specific_msgs_from_queues_and_dlqs() throws JMSException {

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
        final Message jmsMsgQueue = queue.get(0);
        final Message jmsMsgDlq = dlq.get(0);

        // Assert Precondition
        assertThat(queue).isNotEmpty();
        assertThat(dlq).isNotEmpty();

        // Act
        sut.deleteMsg(jmsMsgQueue);
        sut.deleteMsg(jmsMsgDlq);
        sut.deleteMsg(jmsMsgDlq); // tests that code works if you delete a msg that is not there, should be in another test, but no time

        // Assert
        assertThat(sut.listAllMessages()).isEmpty();
        assertThat(sut.listAllMessagesInDlq()).isEmpty();
    }

}
