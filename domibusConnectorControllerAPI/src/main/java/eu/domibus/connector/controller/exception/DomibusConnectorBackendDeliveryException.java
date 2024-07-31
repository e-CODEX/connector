/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

/**
 * Exception thrown when there is an error in delivering a message using Domibus connector backend.
 * This exception is a subclass of DomibusConnectorBackendException.
 */
public class DomibusConnectorBackendDeliveryException extends DomibusConnectorBackendException {
    public DomibusConnectorBackendDeliveryException() {
    }

    public DomibusConnectorBackendDeliveryException(String message) {
        super(message);
    }

    public DomibusConnectorBackendDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomibusConnectorBackendDeliveryException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorBackendDeliveryException(String message, Throwable cause,
                                                    boolean enableSuppression,
                                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
