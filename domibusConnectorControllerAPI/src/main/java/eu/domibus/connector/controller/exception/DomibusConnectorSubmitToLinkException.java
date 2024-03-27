package eu.domibus.connector.controller.exception;

import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;


/**
 * This exception should be thrown by a link implementation
 * when the submission of the message has failed!
 */
public class DomibusConnectorSubmitToLinkException extends DomibusConnectorMessageTransportException {
    public DomibusConnectorSubmitToLinkException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason) {
        super(message, reason);
    }

    public DomibusConnectorSubmitToLinkException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason,
            Throwable cause) {
        super(message, reason, cause);
    }

    public DomibusConnectorSubmitToLinkException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason,
            String reasonMessage, Throwable cause) {
        super(message, reason, reasonMessage, cause);
    }

    public DomibusConnectorSubmitToLinkException(
            DomibusConnectorMessage message,
            String errorMessage) {
        super(message, errorMessage);
    }
}
