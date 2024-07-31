/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.service;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;

/**
 * Generic interface to trigger message pull from LinkPartner.
 */
public interface PullFromLinkPartner {
    void pullMessagesFrom(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);
}
