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

import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Generic interface to submit a message to the connector
 * from any link modules (gwjmsplugin, ...)
 */
public interface SubmitToConnector {
    default void submitToConnector(DomibusConnectorMessage message,
                                   DomibusConnectorLinkPartner linkPartner)
        throws DomibusConnectorSubmitToLinkException {
        submitToConnector(message, linkPartner.getLinkPartnerName(), linkPartner.getLinkType());
    }

    void submitToConnector(
        DomibusConnectorMessage message,
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName, LinkType linkType)
        throws DomibusConnectorSubmitToLinkException;
}
