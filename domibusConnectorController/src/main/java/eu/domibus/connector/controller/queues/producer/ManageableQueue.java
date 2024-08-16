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

import eu.domibus.connector.controller.queues.QueueHelper;
import eu.domibus.connector.controller.service.HasManageableDlq;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import java.util.List;
import javax.jms.Message;
import javax.jms.Queue;

/**
 * The ManageableQueue class is an abstract class that implements the HasManageableDlq interface.
 * It provides common functionality for managing queues and dead letter queues (DLQs).
 *
 * <p>The ManageableQueue class uses a QueueHelper object to perform queue operations.
 * The QueueHelper object encapsulates the details of interacting with the queues
 * and the JmsTemplate.
 *
 * <p>This class provides implementations for the methods defined in the HasManageableDlq interface.
 * These methods allow for putting messages on the queue, getting the queue, getting the queue name,
 * getting the message as text,
 * getting the DLQ, listing all messages in the queue, listing all messages in the DLQ,
 * moving a message from the DLQ to the queue, and deleting a message.
 *
 * <p>To create a specific queue implementation, extend the ManageableQueue class and provide
 * the necessary dependencies for the QueueHelper object.
 * An example usage of the ManageableQueue class can be seen in the following classes:
 * ToConnectorQueue, ToCleanupQueue, ToLinkQueue.
 * These classes extend the ManageableQueue class and provide the necessary dependencies for
 * the QueueHelper object in their constructors.
 *
 * <p>The ManageableQueue class itself is an abstract class and cannot be instantiated directly.
 * It provides a default constructor that accepts a QueueHelper object, which must be provided
 * by the subclass.
 *
 * <p>The ManageableQueue class does not provide any specific example code. However, the usage
 * examples provided above demonstrate how the ManageableQueue class can be extended and used
 * .
 * These examples show the creation of specific queue implementations and the usage of
 * the ManageableQueue methods.
 */
public abstract class ManageableQueue implements HasManageableDlq {
    private final QueueHelper queueHelper;

    ManageableQueue(QueueHelper queueHelper) {
        this.queueHelper = queueHelper;
    }

    @Override
    public void putOnQueue(DomibusConnectorMessage message) {
        queueHelper.putOnQueue(message);
    }

    @Override
    public Queue getQueue() {
        return queueHelper.getDestination();
    }

    @Override
    public String getName() {
        return queueHelper.getName();
    }

    @Override
    public String getMessageAsText(Message msg) {
        return queueHelper.getMessageAsText(msg);
    }

    @Override
    public Queue getDlq() {
        return queueHelper.getDlq();
    }

    @Override
    public List<Message> listAllMessages() {
        return queueHelper.listAllMessages();
    }

    @Override
    public List<Message> listAllMessagesInDlq() {
        return queueHelper.listAllMessagesInDlq();
    }

    @Override
    public void moveMsgFromDlqToQueue(Message msg) {
        queueHelper.moveMsgFromDlqToQueue(msg);
    }

    @Override
    public void deleteMsg(Message msg) {
        queueHelper.deleteMsg(msg);
    }

    @Override
    public String getDlqName() {
        return queueHelper.getDlqName();
    }
}
