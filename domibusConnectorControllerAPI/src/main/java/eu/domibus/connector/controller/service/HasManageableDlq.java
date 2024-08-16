/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
