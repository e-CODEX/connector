/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.queues.producer;

import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CLEANUP_DEAD_LETTER_QUEUE_BEAN;
import static eu.domibus.connector.controller.queues.JmsConfiguration.TO_CLEANUP_QUEUE_BEAN;

import eu.domibus.connector.controller.queues.QueueHelper;
import javax.jms.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * The ToCleanupQueue class is a subclass of ManageableQueue.
 * It represents a queue used for cleanup operations.
 *
 * <p>The ToCleanupQueue class extends ManageableQueue and inherits its methods for managing queues
 * and dead letter queues (DLQs).
 *
 * <p>This class is a Spring component, indicating that it can be managed by the Spring framework.
 *
 * <p>This class has a constructor that accepts a destination queue, a dead letter queue (DLQ),
 * and a JmsTemplate.
 * These dependencies are passed to the superclass constructor, which is defined in
 * the ManageableQueue class.
 *
 * <p>This class does not provide any additional methods or functionality beyond what is inherited
 * from the ManageableQueue class.
 * Its purpose is to provide a specific implementation of the ManageableQueue class for cleanup
 * operations.
 *
 * <p>Usage:
 * ToCleanupQueue qcq = new ToCleanupQueue(destination, dlq, jmsTemplate);
 */
@Component
public class ToCleanupQueue extends ManageableQueue {
    public ToCleanupQueue(@Qualifier(TO_CLEANUP_QUEUE_BEAN) Queue destination,
                          @Qualifier(TO_CLEANUP_DEAD_LETTER_QUEUE_BEAN) Queue dlq,
                          JmsTemplate jmsTemplate) {
        super(new QueueHelper(destination, dlq, jmsTemplate));
    }
}
