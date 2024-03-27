package eu.domibus.connector.controller.exception;

import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public class DomibusConnectorRejectDeliveryException extends DomibusConnectorMessageTransportException {
    public DomibusConnectorRejectDeliveryException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason) {
        super(message, reason);
    }

    public DomibusConnectorRejectDeliveryException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason,
            Throwable cause) {
        super(message, reason, cause);
    }

    public DomibusConnectorRejectDeliveryException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason,
            String reasonMessage, Throwable cause) {
        super(message, reason, reasonMessage, cause);
    }
}
