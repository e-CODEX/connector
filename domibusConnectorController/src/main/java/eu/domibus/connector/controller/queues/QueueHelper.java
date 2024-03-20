package eu.domibus.connector.controller.queues;

import eu.domibus.connector.controller.queues.producer.ToLinkQueue;
import eu.domibus.connector.controller.service.HasManageableDlq;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class QueueHelper implements HasManageableDlq {

    private static final Logger LOGGER = LogManager.getLogger(ToLinkQueue.class);

    @Getter
    private final Queue destination;
    @Getter
    private final Queue dlq;
    private final JmsTemplate jmsTemplate;

    public QueueHelper(Queue destination, Queue dlq, JmsTemplate jmsTemplate) {
        this.destination = destination;
        this.dlq = dlq;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void putOnQueue(DomibusConnectorMessage message) {
        jmsTemplate.convertAndSend(destination, message);
    }

    @Override
    public List<Message> listAllMessages() {
        return list(destination);
    }

    @Override
    public List<Message> listAllMessagesInDlq() {
        return list(dlq);
    }

    private List<Message> list(Queue destination) {
        return jmsTemplate.browse(destination, (BrowserCallback<List<Message>>) (s, qb) -> {
            List<Message> result = new ArrayList<>();
            @SuppressWarnings("unchecked") final Enumeration<Message> enumeration = qb.getEnumeration();
            while (enumeration.hasMoreElements()) {
                final Message message = enumeration.nextElement();
                result.add(message);
            }
            return result;
        });
    }

    // TODO this is a replacement for the method below, this method does not depend on a destination. It can restore any dlq message to the queue where it failed processing.
    private void moveAnyDlqMessageBackToItsOrigQueue(Message msg) {
        try {
            final Message message = jmsTemplate.receiveSelected(msg.getJMSDestination(), "JMSMessageID = '" + msg.getJMSMessageID() + "'");
            jmsTemplate.send(msg.getJMSDestination().toString().replace("DLQ.", ""), session -> msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // TODO this is not working, throws:
//      XA resource 'jmsConnectionFactory': commit for XID 'bla.bla' raised -4: the supplied XID is invalid for this XA resource
    @Override
    public void moveMsgFromDlqToQueue(Message msg) {
        try {
            final DomibusConnectorMessage c = (DomibusConnectorMessage) jmsTemplate.receiveSelectedAndConvert(msg.getJMSDestination(), "JMSMessageID = '" + msg.getJMSMessageID() + "'");
            putOnQueue(c);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMsg(Message msg) {
        try {
            final Message m =
                    jmsTemplate.receiveSelected(
                            msg.getJMSDestination(),
                            "JMSMessageID = '" + msg.getJMSMessageID() + "'");

            if (m != null) {
                m.acknowledge();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        try {
            return destination.getQueueName();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getMessageAsText(Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            try {
                return ((TextMessage) msg).getText();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("The provided message is not a text message!");
    }

    @Override
    public String getDlqName() {
        try {
            return dlq.getQueueName();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Queue getQueue() {
        return destination;
    }
}
