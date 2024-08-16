/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.exception;

import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;

/**
 * Represents an exception that occurs when a message submission is rejected by
 * the Domibus connector.
 * It is a subclass of DomibusConnectorMessageTransportException.
 */
public class DomibusConnectorRejectSubmissionException
    extends DomibusConnectorMessageTransportException {
    public DomibusConnectorRejectSubmissionException(DomibusConnectorMessage message,
                                                     DomibusConnectorRejectionReason reason) {
        super(message, reason);
    }

    public DomibusConnectorRejectSubmissionException(DomibusConnectorMessage message,
                                                     DomibusConnectorRejectionReason reason,
                                                     Throwable cause) {
        super(message, reason, cause);
    }

    public DomibusConnectorRejectSubmissionException(DomibusConnectorMessage message,
                                                     DomibusConnectorRejectionReason reason,
                                                     String reasonMessage, Throwable cause) {
        super(message, reason, reasonMessage, cause);
    }
}
