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

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ListMessagesInQueueTest.MyTestContext.class}, properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles({"test", "jms-test"})
@DirtiesContext
@Disabled //TODO: find solution so all tests can run in same spring test context, or reduce spring context size
public class ListMessagesInQueueTest {

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
    public void it_is_possible_to_retrieve_all_messages_that_are_on_the_dlq() throws JMSException {

        // Arrange
        final QueueHelper sut = new QueueHelper(q1, dlq1, nonXaJmsTemplate);
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("msg1"));
        nonXaJmsTemplate.convertAndSend(dlq1, message); // put something on the dlq

        // Act
        final List<Message> messages = sut.listAllMessagesInDlq();

        // Assert
        final DomibusConnectorMessage domainMsg = (DomibusConnectorMessage) converter.fromMessage(messages.get(0)); // convert jms to domain msg
        assertThat(domainMsg.getConnectorMessageId().getConnectorMessageId()).isEqualTo("msg1");
    }
}
