/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.security.exception;

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
