package eu.domibus.connector.controller.service;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;


/**
 * Will be called by the connector
 * controller to submit a message to a link
 * the specific implementation here has to look up the
 * correct link partner
 */
public interface SubmitToLinkService {
    void submitToLink(DomibusConnectorMessage message) throws DomibusConnectorSubmitToLinkException;
}
