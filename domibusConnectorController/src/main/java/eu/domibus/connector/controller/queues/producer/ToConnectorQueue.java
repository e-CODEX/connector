/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.queues.producer;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN;
import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CONNECTOR_QUEUE_BEAN;

import eu.domibus.connector.controller.queues.QueueHelper;
import jakarta.jms.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * The ToConnectorQueue class represents a queue for sending messages to a connector.
 * It extends the ManageableQueue class and provides functionality for managing the queue
 * and the associated dead letter queue (DLQ).
 *
 * <p>The ToConnectorQueue class uses a QueueHelper object to perform queue operations.
 * The QueueHelper object encapsulates the details of interacting with the queue
 * and the JmsTemplate.
 *
 * <p>It provides implementations for the methods defined in the ManageableQueue class,
 * which include
 * putting messages on the queue, getting the queue, getting the queue name, getting the message
 * as text,
 * getting the DLQ, listing all messages in the queue, listing all messages in the DLQ,
 * moving a message from the DLQ to the queue, and deleting a message.
 *
 * <p>Example usage can be seen in the MoveMessagesFromDlqToQueueTest and
 * DeleteMessagesFromQueuesTest classes, where objects of ToConnectorQueue are used to perform
 * various queue operations.
 *
 * @see ManageableQueue
 * @see QueueHelper
 */
@Component
public class ToConnectorQueue extends ManageableQueue {
    public ToConnectorQueue(@Qualifier(TO_CONNECTOR_QUEUE_BEAN) Queue destination,
                            @Qualifier(TO_CONNECTOR_DEAD_LETTER_QUEUE_BEAN) Queue dlq,
                            JmsTemplate jmsTemplate) {
        super(new QueueHelper(destination, dlq, jmsTemplate));
    }
}
