package eu.domibus.connector.controller.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

public interface PutOnQueue {

    public void putOnQueue(DomibusConnectorMessage message);

    public javax.jms.Queue getQueue();

}
