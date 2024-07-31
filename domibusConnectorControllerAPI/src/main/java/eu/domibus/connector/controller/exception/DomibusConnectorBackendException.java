/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.exception;

/**
 * {@code DomibusConnectorBackendException} is an exception class that is thrown when there is
 * an error in delivering a message using the Domibus connector backend.
 * It is a subclass of the {@link RuntimeException} class.
 *
 * <p>This class provides multiple constructors to allow specifying a custom message, a cause,
 * and additional flags.</p>
 */
public class DomibusConnectorBackendException extends RuntimeException {
    public DomibusConnectorBackendException() {
    }

    public DomibusConnectorBackendException(String message) {
        super(message);
    }

    public DomibusConnectorBackendException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomibusConnectorBackendException(Throwable cause) {
        super(cause);
    }

    public DomibusConnectorBackendException(String message, Throwable cause,
                                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
