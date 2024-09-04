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
