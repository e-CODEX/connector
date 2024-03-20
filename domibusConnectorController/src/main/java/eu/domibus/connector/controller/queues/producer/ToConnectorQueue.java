package eu.domibus.connector.controller.queues.producer;

import eu.domibus.connector.controller.queues.QueueHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN;
import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CONNECTOR_QUEUE_BEAN;

@Component
public class ToConnectorQueue extends ManageableQueue {

    public ToConnectorQueue(@Qualifier(TO_CONNECTOR_QUEUE_BEAN) Queue destination,
                            @Qualifier(TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN) Queue dlq,
                            JmsTemplate jmsTemplate)
    {
        super(new QueueHelper(destination, dlq, jmsTemplate));
    }
}
