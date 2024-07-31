/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.service;

import java.util.List;
import javax.jms.Message;
import javax.jms.Queue;

/**
 * This interface represents a class that has a manageable dead letter queue (DLQ).
 */
public interface HasManageableDlq extends PutOnQueue {
    Queue getDlq();

    List<Message> listAllMessages();

    List<Message> listAllMessagesInDlq();

    void moveMsgFromDlqToQueue(Message msg);

    void deleteMsg(Message msg);

    String getName();

    String getMessageAsText(Message msg);

    String getDlqName();
}
