/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.exception;

/**
 * The DomibusConnectorSecurityException class is a runtime exception that is thrown when a
 * security-related issue occurs in the Domibus connector. This exception extends the
 * RuntimeException class.
 */
public class DomibusConnectorSecurityException extends RuntimeException {
    private static final long serialVersionUID = 8837848390850284261L;

    public DomibusConnectorSecurityException() {
    }

    public DomibusConnectorSecurityException(String arg0) {
        super(arg0);
    }

    public DomibusConnectorSecurityException(Throwable arg0) {
        super(arg0);
    }

    public DomibusConnectorSecurityException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
