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
 * Represents an exception that occurs during the transport of a message within the Domibus
 * connector.
 */
public class DomibusConnectorMessageTransportException extends RuntimeException {
    private DomibusConnectorMessage connectorMessage;
    private DomibusConnectorRejectionReason reason;
    private boolean retryable = false;
    private ErrorCode errorCode;

    public DomibusConnectorMessageTransportException(DomibusConnectorMessage message,
                                                     DomibusConnectorRejectionReason reason) {
        this.connectorMessage = message;
        this.reason = reason;
    }

    /**
     * Creates a new instance of DomibusConnectorMessageTransportException with the specified
     * DomibusConnectorMessage,
     * DomibusConnectorRejectionReason, and Throwable cause.
     *
     * @param message The DomibusConnectorMessage associated with the exception.
     * @param reason The DomibusConnectorRejectionReason indicating the reason for the exception.
     * @param cause The Throwable cause of the exception.
     */
    public DomibusConnectorMessageTransportException(DomibusConnectorMessage message,
                                                     DomibusConnectorRejectionReason reason,
                                                     Throwable cause) {
        super(cause);
        this.connectorMessage = message;
        this.reason = reason;
    }

    /**
     * Represents an exception that occurs during the transport of a message within the Domibus
     * connector.
     */
    public DomibusConnectorMessageTransportException(DomibusConnectorMessage message,
                                                     DomibusConnectorRejectionReason reason,
                                                     String reasonMessage, Throwable cause) {
        super(reasonMessage, cause);
        this.connectorMessage = message;
        this.reason = reason;
    }

    public DomibusConnectorMessageTransportException(DomibusConnectorMessage message,
                                                     String errorMessage) {
        super(errorMessage);
        this.connectorMessage = message;
    }

    public DomibusConnectorMessage getConnectorMessage() {
        return connectorMessage;
    }

    public DomibusConnectorRejectionReason getReason() {
        return reason;
    }

    public void setConnectorMessage(DomibusConnectorMessage connectorMessage) {
        this.connectorMessage = connectorMessage;
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
