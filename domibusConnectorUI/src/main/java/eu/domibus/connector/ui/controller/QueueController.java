package eu.domibus.connector.ui.controller;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import eu.domibus.connector.controller.queues.producer.ManageableQueue;
import eu.domibus.connector.ui.dto.WebQueue;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.InvalidDestinationException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class QueueController {

    private static final Logger LOGGER = LogManager.getLogger(QueueController.class);

    @Getter
    private final MessageConverter converter;

    @Getter
    private final Map<String, ManageableQueue> queueMap;
    private final Map<String, ManageableQueue> errorQueueMap;

    public QueueController(@Autowired(required = false) List<ManageableQueue> queues, MessageConverter converter) {
        if (queues == null) {
            this.queueMap = new HashMap<>();
            this.errorQueueMap = new HashMap<>();
        } else {
            this.queueMap = queues.stream().collect(Collectors.toMap(ManageableQueue::getName, q -> q));
            this.errorQueueMap = queues.stream().collect(Collectors.toMap(ManageableQueue::getDlqName, q -> q));
        }
        this.converter = converter;
    }

    @Transactional
    public void deleteMsg(Message msg) {
        // can be any queue
        queueMap.values().stream().findFirst().ifPresent(q -> q.deleteMsg(msg));
    }

    public String getMsgText(Message msg) {
        // can be any queue
        return queueMap.values().stream().findFirst().map(q -> q.getMessageAsText(msg)).orElse("[none]");
    }

    @Transactional
    public void moveMsgFromDlqToQueue(Message msg) {
        String jmsDestination = null;
        try {
            if (msg.getJMSDestination() instanceof javax.jms.Queue) {
                jmsDestination = ((Queue) msg.getJMSDestination()).getQueueName();
            } else {
                String error = "Illegal destination: [" + msg.getJMSDestination() + "] Other destinations then queues are not supported!";
                LOGGER.warn(error);
                Notification.show(error);
            }
            if (jmsDestination != null) {
                ManageableQueue manageableQueue = errorQueueMap.get(jmsDestination);
                if (manageableQueue == null) {
                    throw new IllegalArgumentException(String.format("DLQ with name [%s] was not found! Available are: [%s]",
                            jmsDestination,
                            errorQueueMap.keySet().stream().collect(Collectors.joining(","))
                    ));
                }
                manageableQueue.moveMsgFromDlqToQueue(msg);
            } else {
                String error = "Illegal destination: null";
                Notification.show(error);
                LOGGER.warn(error);
            }
        } catch (JMSException e) {
            String error = "An exception occured while moving message from DLQ to queue";
            LOGGER.warn(error, e);
           Notification.show(error);
        }
    }

    @Transactional
    public List<WebQueue> getQueues() {
        List<WebQueue> result = queueMap.values().stream()
                .map(this::mapQueueToWebQueue)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return result;
    }

    private WebQueue mapQueueToWebQueue(ManageableQueue manageableQueue) {
        try {
            WebQueue webQueue = new WebQueue();

            webQueue.setName(manageableQueue.getName());
            final List<Message> cleanupMsgs = manageableQueue.listAllMessages();
            webQueue.setMessages(cleanupMsgs);
            webQueue.setMsgsOnQueue(cleanupMsgs.size());
            try {
                final List<Message> cleanupDlqMsgs = manageableQueue.listAllMessagesInDlq();
                webQueue.setDlqMessages(cleanupDlqMsgs);
                webQueue.setMsgsOnDlq(cleanupDlqMsgs.size());
            } catch (InvalidDestinationException ide) {
                LOGGER.trace("Error occured while reading from DLQ [" + manageableQueue.getDlqName() + "] (maybe the queue has not been created yet)", ide);
            } catch (Exception e) {
                LOGGER.warn("Error occured while reading from DLQ [" + manageableQueue.getDlqName() + "]", e);
            }

            return webQueue;
        } catch (Exception e) {
            LOGGER.warn("Error occured", e);
            return null;
        }
    }

    public void showMessage(Message message) {
        String messageText = this.getMsgText(message);
        Dialog d = new Dialog();
        d.add(new Label(messageText));
        d.open();
        d.setSizeFull();
    }
}
