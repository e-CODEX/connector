/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.queues.producer;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_LINK_DEAD_LETTER_QUEUE_BEAN;
import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_LINK_QUEUE_BEAN;

import eu.domibus.connector.controller.queues.QueueHelper;
import javax.jms.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * The ToLinkQueue class is a subclass of ManageableQueue and represents a specific
 * queue implementation for linking messages.
 *
 * <p>The ToLinkQueue class provides a constructor that accepts the following parameters:
 * - destination: the queue where messages will be sent
 * - dlq: the dead letter queue where failed messages will be moved
 * - jmsTemplate: the JmsTemplate used for sending messages to the queue
 *
 * <p>The ToLinkQueue class extends ManageableQueue and uses a QueueHelper object to perform
 * queue operations.
 * The QueueHelper object encapsulates the details of interacting with the queues and
 * the JmsTemplate.
 *
 * <p>The ToLinkQueue class overrides the following methods inherited from ManageableQueue:
 * - putOnQueue: sends a message to the destination queue
 * - getQueue: returns the destination queue
 * - getName: returns the name of the destination queue
 * - getMessageAsText: returns the text of a given message as a String
 * - getDlq: returns the dead letter queue
 * - listAllMessages: lists all messages in the destination queue
 * - listAllMessagesInDlq: lists all messages in the dead letter queue
 * - moveMsgFromDlqToQueue: moves a specific message from the dead letter queue to the destination
 * queue
 * - deleteMsg: deletes a specific message from the destination queue
 * - getDlqName: returns the name of the dead letter queue
 *
 * <p>The ToLinkQueue class does not provide any specific example code. However, it demonstrates
 * how to create a specific queue implementation by extending the ManageableQueue class and
 * providing the necessary dependencies for the QueueHelper object in the constructor.
 */
@Component
public class ToLinkQueue extends ManageableQueue {
    public ToLinkQueue(@Qualifier(TO_LINK_QUEUE_BEAN) Queue destination,
                       @Qualifier(TO_LINK_DEAD_LETTER_QUEUE_BEAN) Queue dlq,
                       JmsTemplate jmsTemplate) {
        super(new QueueHelper(destination, dlq, jmsTemplate));
    }
}
