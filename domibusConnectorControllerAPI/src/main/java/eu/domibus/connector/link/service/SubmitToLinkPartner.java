package eu.domibus.connector.link.service;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;


/**
 * Generic interface for submitting a message
 * to a link (eg. gateway-ws-link, gateway-jms-link, backend-ws-link, ...)
 */
public interface SubmitToLinkPartner {
    void submitToLink(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) throws DomibusConnectorSubmitToLinkException;
}
