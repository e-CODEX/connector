/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * This interface represents a class that puts a message on a queue and provides access to
 * the queue.
 */
public interface PutOnQueue {
    void putOnQueue(DomibusConnectorMessage message);

    jakarta.jms.Queue getQueue();
}
