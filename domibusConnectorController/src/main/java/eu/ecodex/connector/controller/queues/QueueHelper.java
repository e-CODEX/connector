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

import eu.ecodex.connector.controller.service.HasManageableDlq;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import lombok.Getter;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

/**
 * The QueueHelper class is a utility class that provides methods for managing queues and dead
 * letter queues. It implements the HasManageableDlq interface.
 *
 * <p>The QueueHelper class provides a constructor that accepts the following parameters:
 * - destination: the queue where messages will be sent - dlq: the dead letter queue where failed
 * messages will be moved - jmsTemplate: the JmsTemplate used for interacting with the queues
 *
 * <p>The QueueHelper class implements the methods defined in the HasManageableDlq interface.
 * These methods allow users to put messages on the queue, list all messages in the queue and dead
 * letter queue, move messages from the dead letter queue to the original queue, delete messages
 * from the original queue, retrieve the name and text of a message, and get the name of the dead
 * letter queue.
 *
 * <p>The QueueHelper class also defines some private helper methods for browsing and operating
 * on the queues.
 *
 * <p>Example usage:
 * <pre>
 *     QueueHelper queueHelper = new QueueHelper(destinationQueue, dlqQueue, jmsTemplate);
 *     queueHelper.putOnQueue(message);
 *     List&lt;Message> messages = queueHelper.listAllMessages();
 *     List&lt;Message> dlqMessages = queueHelper.listAllMessagesInDlq();
 *     queueHelper.moveMsgFromDlqToQueue(message);
 *     queueHelper.deleteMsg(message);
 *     String queueName = queueHelper.getName();
 *     String messageText = queueHelper.getMessageAsText(message);
 *     String dlqName = queueHelper.getDlqName();
 * </pre>
 */
public class QueueHelper implements HasManageableDlq {
    public static final String JMS_MESSAGE_ID_SELECTOR = "JMSMessageID";
    private static final Logger LOGGER = LogManager.getLogger(QueueHelper.class);
    @Getter
    private final Queue destination;
    @Getter
    private final Queue dlq;
    private final JmsTemplate jmsTemplate;

    /**
     * The QueueHelper class provides helper methods for interacting with a queue and a dead letter
     * queue (DLQ). It encapsulates the details of interacting with the queue and the JmsTemplate.
     *
     * <p>Example usage can be seen in the ToConnectorQueue, ToCleanupQueue, and ToLinkQueue
     * classes, where objects of QueueHelper are used to perform various queue operations.
     */
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
        return listMessages(destination);
    }

    @Override
    public List<Message> listAllMessagesInDlq() {
        return listMessages(dlq);
    }

    private List<Message> listMessages(Queue destination) {
        return jmsTemplate.browse(destination, (s, qb) -> {
            List<Message> result = new ArrayList<>();
            @SuppressWarnings("unchecked")
            final Enumeration<Message> enumeration = qb.getEnumeration();
            while (enumeration.hasMoreElements()) {
                final var message = enumeration.nextElement();
                try {
                    LOGGER.debug("MESSAGE JMS DESTINATION: {}, JMSMessageID: {}, browsing from: {}",
                            message.getJMSDestination(),
                            message.getJMSMessageID(),
                            destination.getQueueName());
                } catch (JMSException e) {
                    LOGGER.warn("Error reading message properties", e);
                }
                result.add(message);
            }
            return result;
        });
    }

    private String generateMessageSelector(Message message) {
        try {
            return String.format("%s = '%s'", JMS_MESSAGE_ID_SELECTOR, message.getJMSMessageID());
        } catch (JMSException e) {
            LOGGER.debug(e.getMessage());
            return "";
        }
    }

    private Message receiveMessageFromDlq(Message message) {
        var messageSelector = this.generateMessageSelector(message);
        try {
            return jmsTemplate.receiveSelected(this.dlq, messageSelector);
        } catch (JmsException e) {
            LOGGER.warn("Failed to receive using Queue object, trying FQQN: {}", e.getMessage());
            ActiveMQDestination activeMQdestination;
            try {
                activeMQdestination = (ActiveMQDestination) message.getJMSDestination();
            } catch (JMSException ex) {
                throw new RuntimeException(ex);
            }

            if (activeMQdestination != null) {
                String fullQualifiedQueueName = String.format(
                        "%s::%s", activeMQdestination.getAddress(), activeMQdestination.getName()
                );

                LOGGER.warn("Trying FQQN format: {}", fullQualifiedQueueName);
                var received = jmsTemplate.receiveSelected(fullQualifiedQueueName, messageSelector);

                if (received != null) {
                    LOGGER.warn("Successfully received message using FQQN: {}", received);
                    return received;
                }
                LOGGER.warn("Failed receiving message using FQQN: {}", fullQualifiedQueueName);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void moveMessageFromDlqToQueue(Message message) {
        try {
            var receivedMessage = this.receiveMessageFromDlq(message);
            if (receivedMessage != null) {
                // send the message to the original queue
                jmsTemplate.send(this.destination, session -> message);
            }

        } catch (JmsException e) {
            LOGGER.debug(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteMessage(Message message) {
        try {
            this.receiveMessageFromDlq(message);
        } catch (JmsException e) {
            LOGGER.debug(e.getMessage());
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
    public String getMessageAsText(Message message) {
        if (message instanceof TextMessage textMessage) {
            try {
                return textMessage.getText();
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
