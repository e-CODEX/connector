package eu.domibus.connector.controller.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public interface PutOnQueue {
    void putOnQueue(DomibusConnectorMessage message);

    javax.jms.Queue getQueue();
}
