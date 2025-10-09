/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.controller;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import eu.ecodex.connector.controller.queues.producer.ManageableQueue;
import eu.ecodex.connector.ui.dto.WebQueue;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.InvalidDestinationException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The QueueController class is a controller that handles operations on queues and messages. It
 * provides methods for deleting a message, getting the text of a message, moving a message from the
 * dead letter queue (DLQ) to a queue, and getting a list of queues.
 *
 * @see ManageableQueue
 * @see WebQueue
 */
@Component
public class QueueController {
    private static final Logger LOGGER = LogManager.getLogger(QueueController.class);
    @Getter
    private final MessageConverter converter;
    @Getter
    private final Map<String, ManageableQueue> queueMap;
    private final Map<String, ManageableQueue> errorQueueMap;

    /**
     * The QueueController class is responsible for managing queues in the system.
     *
     * @param queues    A list of ManageableQueue objects (optional). If provided, queues will be
     *                  added to the queueMap and errorQueueMap.
     * @param converter A MessageConverter object used to convert messages.
     * @see ManageableQueue
     */
    public QueueController(
        @Autowired(required = false) List<ManageableQueue> queues, MessageConverter converter) {
        if (queues == null) {
            this.queueMap = new HashMap<>();
            this.errorQueueMap = new HashMap<>();
        } else {
            this.queueMap =
                queues.stream().collect(Collectors.toMap(ManageableQueue::getName, q -> q));
            this.errorQueueMap =
                queues.stream().collect(Collectors.toMap(ManageableQueue::getDlqName, q -> q));
        }
        this.converter = converter;
    }

    /**
     * Deletes a given JMS message from the associated queue. If the destination is not a valid
     * queue or is null, a warning is logged, and a notification is displayed. Handles any
     * potential JMS exceptions during the deletion process.
     *
     * @param message The JMS message to be deleted. The message should contain a valid destination
     *            that corresponds to a queue.
     */
    @Transactional
    public void deleteMessage(Message message) {
        try {
            var destination = (ActiveMQDestination) message.getJMSDestination();
            if (destination != null) {

                var jmsDestination = destination.getName();
                var manageableQueue = errorQueueMap.get(jmsDestination);
                manageableQueue.deleteMessage(message);
            } else {
                String error = String.format(
                        "Illegal destination: [%s]. "
                                + "Other destinations than queues are not supported!",
                        message.getJMSDestination()
                );
                LOGGER.warn(error);
                Notification.show(error);
            }
        } catch (JMSException e) {
            var error = "An exception occurred while deleting message from DLQ";
            LOGGER.warn(error, e);
            Notification.show(error);
        }
    }

    /**
     * Retrieves the text content of a message.
     *
     * @param message The message for which to retrieve the text content.
     * @return The text content of the message. If the message is a TextMessage, the text value of
     *      the message is returned. If the message is not a TextMessage, an
     *      IllegalArgumentException is thrown.
     */
    public String getMessageText(Message message) {
        // can be any queue
        return queueMap
                .values()
                .stream()
                .findFirst()
                .map(q -> q.getMessageAsText(message))
                .orElse("[none]");
    }

    /**
     * Moves a message from the dead letter queue (DLQ) to the original queue.
     *
     * @param message The message to be moved. The message should be an instance
     *                of jakarta.jms.Message.
     * @throws IllegalArgumentException If the message's destination is not a queue.
     * @throws IllegalArgumentException If the DLQ with the given name is not found.
     */
    @Transactional
    public void moveMessageFromDlqToQueue(Message message) {
        String jmsDestination = null;
        try {
            var destination = (ActiveMQDestination) message.getJMSDestination();
            if (destination != null) {
                jmsDestination = destination.getName();
            } else {
                String error = "Illegal destination: [" + message.getJMSDestination()
                    + "] Other destinations then queues are not supported!";
                LOGGER.warn(error);
                Notification.show(error);
            }
            if (jmsDestination != null) {
                System.out.println("MOVE DESTINATION: " + jmsDestination);
                var manageableQueue = errorQueueMap.get(jmsDestination);
                if (manageableQueue == null) {
                    throw new IllegalArgumentException(String.format(
                        "DLQ with name [%s] was not found! Available are: [%s]",
                        jmsDestination,
                        String.join(",", errorQueueMap.keySet())
                    ));
                }
                manageableQueue.moveMessageFromDlqToQueue(message);
            } else {
                var error = "Illegal destination: null";
                Notification.show(error);
                LOGGER.warn(error);
            }
        } catch (JMSException e) {
            var error = "An exception occurred while moving message from DLQ to queue";
            LOGGER.warn(error, e);
            Notification.show(error);
        }
    }

    /**
     * Retrieves a list of WebQueue objects representing the queues in the system.
     *
     * <p>This method retrieves all the queues from the `queueMap`, maps each ManageableQueue
     * object to a WebQueue object, and filter out any null values. The resulting WebQueue objects
     * are collected into a List and returned.
     *
     * @return a list of WebQueue objects representing the queues in the system
     */
    @Transactional
    public List<WebQueue> getQueues() {
        return queueMap
                .values()
                .stream()
                .map(this::mapQueueToWebQueue)
                .filter(Objects::nonNull)
                .toList();
    }

    private WebQueue mapQueueToWebQueue(ManageableQueue manageableQueue) {
        try {
            var webQueue = new WebQueue();
            webQueue.setName(manageableQueue.getName());
            final var cleanupMessages = manageableQueue.listAllMessages();
            webQueue.setMessages(cleanupMessages);
            webQueue.setMessagesOnQueue(cleanupMessages.size());

            try {
                final List<Message> cleanupDlqMessages = manageableQueue.listAllMessagesInDlq();
                webQueue.setDlqMessages(cleanupDlqMessages);
                webQueue.setMessagesOnDlq(cleanupDlqMessages.size());
            } catch (InvalidDestinationException ide) {
                var error = String.format(
                        "Error occurred while reading from DLQ [%s]. "
                                + "(maybe the queue has not been created yet)",
                        manageableQueue.getDlqName()
                );
                LOGGER.trace(error, ide);
            } catch (Exception e) {
                LOGGER.warn(
                    "Error occurred while reading from DLQ: [{}]", manageableQueue.getDlqName(), e
                );
            }

            return webQueue;
        } catch (Exception e) {
            LOGGER.warn("Error occurred while mapping queue to WebQueue", e);
            return null;
        }
    }

    /**
     * Displays a message in a dialog box.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(Message message) {
        String messageText = this.getMessageText(message);
        var d = new Dialog();
        d.add(new NativeLabel(messageText));
        d.open();
        d.setSizeFull();
    }
}
