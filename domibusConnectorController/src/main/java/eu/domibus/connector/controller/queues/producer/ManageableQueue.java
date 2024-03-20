package eu.domibus.connector.controller.queues.producer;

import eu.domibus.connector.controller.queues.QueueHelper;
import eu.domibus.connector.controller.service.HasManageableDlq;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;

import javax.jms.Message;
import javax.jms.Queue;
import java.util.List;

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
