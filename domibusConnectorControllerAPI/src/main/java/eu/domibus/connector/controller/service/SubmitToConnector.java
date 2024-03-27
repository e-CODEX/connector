package eu.domibus.connector.controller.service;

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;


/**
 * Generic interface to submit a message to the connector
 * from any link modules (gwjmsplugin, ...)
 */
public interface SubmitToConnector {
    default void submitToConnector(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner linkPartner) throws DomibusConnectorSubmitToLinkException {
        submitToConnector(message, linkPartner.getLinkPartnerName(), linkPartner.getLinkType());
    }

    void submitToConnector(
            DomibusConnectorMessage message, DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName
            , LinkType linkType) throws DomibusConnectorSubmitToLinkException;
}
