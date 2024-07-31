/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
