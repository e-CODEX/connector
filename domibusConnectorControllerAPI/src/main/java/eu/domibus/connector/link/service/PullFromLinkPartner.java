package eu.domibus.connector.link.service;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;


/**
 * Generic interface to trigger message
 * pull from LinkPartner
 */
public interface PullFromLinkPartner {
    void pullMessagesFrom(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);
}
