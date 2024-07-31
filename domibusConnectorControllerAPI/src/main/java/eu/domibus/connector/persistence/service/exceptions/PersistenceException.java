/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.exceptions;

/**
 * The PersistenceException class is a subclass of the RuntimeException class. It is thrown when
 * there is an error in the persistence of data.
 */
public class PersistenceException extends RuntimeException {
    private static final long serialVersionUID = -5879716562932550680L;

    public PersistenceException() {
    }

    public PersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public PersistenceException(String arg0) {
        super(arg0);
    }

    public PersistenceException(Throwable arg0) {
        super(arg0);
    }
}
