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
 * This exception should be thrown by a link implementation
 * when the submission of the message has failed.
 */
public class DomibusConnectorSubmitToLinkException
    extends DomibusConnectorMessageTransportException {
    public DomibusConnectorSubmitToLinkException(DomibusConnectorMessage message,
                                                 DomibusConnectorRejectionReason reason) {
        super(message, reason);
    }

    public DomibusConnectorSubmitToLinkException(DomibusConnectorMessage message,
                                                 DomibusConnectorRejectionReason reason,
                                                 Throwable cause) {
        super(message, reason, cause);
    }

    public DomibusConnectorSubmitToLinkException(DomibusConnectorMessage message,
                                                 DomibusConnectorRejectionReason reason,
                                                 String reasonMessage, Throwable cause) {
        super(message, reason, reasonMessage, cause);
    }

    public DomibusConnectorSubmitToLinkException(DomibusConnectorMessage message,
                                                 String errorMessage) {
        super(message, errorMessage);
    }
}
