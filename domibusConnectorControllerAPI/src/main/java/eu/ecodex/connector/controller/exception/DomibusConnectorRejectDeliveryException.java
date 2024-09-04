/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.exception;

import eu.ecodex.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;

/**
 * Represents an exception that occurs when the delivery of a message is rejected by
 * the Domibus connector. This exception is a subclass of DomibusConnectorMessageTransportException.
 */
public class DomibusConnectorRejectDeliveryException
    extends DomibusConnectorMessageTransportException {
    public DomibusConnectorRejectDeliveryException(DomibusConnectorMessage message,
                                                   DomibusConnectorRejectionReason reason) {
        super(message, reason);
    }

    public DomibusConnectorRejectDeliveryException(DomibusConnectorMessage message,
                                                   DomibusConnectorRejectionReason reason,
                                                   Throwable cause) {
        super(message, reason, cause);
    }

    public DomibusConnectorRejectDeliveryException(DomibusConnectorMessage message,
                                                   DomibusConnectorRejectionReason reason,
                                                   String reasonMessage, Throwable cause) {
        super(message, reason, reasonMessage, cause);
    }
}
