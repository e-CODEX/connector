package eu.domibus.connector.controller.exception;

import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;


public class DomibusConnectorMessageTransportException extends RuntimeException {
    private DomibusConnectorMessage connectorMessage;
    private DomibusConnectorRejectionReason reason;
    private boolean retryable = false;
    private ErrorCode errorCode;

    public DomibusConnectorMessageTransportException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason) {
        this.connectorMessage = message;
        this.reason = reason;
    }

    public DomibusConnectorMessageTransportException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason,
            Throwable cause) {
        super(cause);
        this.connectorMessage = message;
        this.reason = reason;
    }

    public DomibusConnectorMessageTransportException(
            DomibusConnectorMessage message,
            DomibusConnectorRejectionReason reason,
            String reasonMessage, Throwable cause) {
        super(reasonMessage, cause);
        this.connectorMessage = message;
        this.reason = reason;
    }

    public DomibusConnectorMessageTransportException(DomibusConnectorMessage message, String errorMessage) {
        super(errorMessage);
        this.connectorMessage = message;
    }

    public DomibusConnectorMessage getConnectorMessage() {
        return connectorMessage;
    }

    public void setConnectorMessage(DomibusConnectorMessage connectorMessage) {
        this.connectorMessage = connectorMessage;
    }

    public DomibusConnectorRejectionReason getReason() {
        return reason;
    }

    public void setReason(DomibusConnectorRejectionReason reason) {
        this.reason = reason;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
