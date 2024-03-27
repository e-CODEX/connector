package eu.domibus.connector.controller.processor;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Connector Controller internal API
 * <p>
 * used to process messages (business messages, confirmation messages)
 * going through the connector
 */
public interface DomibusConnectorMessageProcessor {
    void processMessage(DomibusConnectorMessage message);
}
