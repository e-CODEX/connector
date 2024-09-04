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

    @Transactional
    public void deleteMsg(Message msg) {
        // can be any queue
        queueMap.values().stream().findFirst().ifPresent(q -> q.deleteMsg(msg));
    }

    /**
     * Retrieves the text content of a message.
     *
     * @param msg The message for which to retrieve the text content.
     * @return The text content of the message. If the message is a TextMessage, the text value of
     *      the message is returned. If the message is not a TextMessage, an
     *      IllegalArgumentException is thrown.
     */
    public String getMsgText(Message msg) {
        // can be any queue
        return queueMap.values().stream().findFirst().map(q -> q.getMessageAsText(msg))
                       .orElse("[none]");
    }

    /**
     * Moves a message from the dead letter queue (DLQ) to the original queue.
     *
     * @param msg The message to be moved. The message should be an instance of javax.jms.Message.
     * @throws IllegalArgumentException If the message's destination is not a queue.
     * @throws IllegalArgumentException If the DLQ with the given name is not found.
     */
    @Transactional
    public void moveMsgFromDlqToQueue(Message msg) {
        String jmsDestination = null;
        try {
            if (msg.getJMSDestination() instanceof jakarta.jms.Queue queue) {
                jmsDestination = queue.getQueueName();
            } else {
                String error = "Illegal destination: [" + msg.getJMSDestination()
                    + "] Other destinations then queues are not supported!";
                LOGGER.warn(error);
                Notification.show(error);
            }
            if (jmsDestination != null) {
                var manageableQueue = errorQueueMap.get(jmsDestination);
                if (manageableQueue == null) {
                    throw new IllegalArgumentException(String.format(
                        "DLQ with name [%s] was not found! Available are: [%s]",
                        jmsDestination,
                        String.join(",", errorQueueMap.keySet())
                    ));
                }
                manageableQueue.moveMsgFromDlqToQueue(msg);
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
        return queueMap.values().stream()
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
            webQueue.setMsgsOnQueue(cleanupMessages.size());

            try {
                final List<Message> cleanupDlqMessages = manageableQueue.listAllMessagesInDlq();
                webQueue.setDlqMessages(cleanupDlqMessages);
                webQueue.setMsgsOnDlq(cleanupDlqMessages.size());
            } catch (InvalidDestinationException ide) {
                LOGGER.trace(
                    "Error occurred while reading from DLQ [{}] (maybe the queue has not "
                        + "been created yet)", manageableQueue.getDlqName(), ide);
            } catch (Exception e) {
                LOGGER.warn(
                    "Error occurred while reading from DLQ [{}]", manageableQueue.getDlqName(), e
                );
            }

            return webQueue;
        } catch (Exception e) {
            LOGGER.warn("Error occurred", e);
            return null;
        }
    }

    /**
     * Displays a message in a dialog box.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(Message message) {
        String messageText = this.getMsgText(message);
        var d = new Dialog();
        d.add(new NativeLabel(messageText));
        d.open();
        d.setSizeFull();
    }
}
