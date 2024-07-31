/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.exceptions;

/**
 * Thrown to indicate that the result size is incorrect. This exception is a sub-class of
 * PersistenceException.
 */
public class IncorrectResultSizeException extends PersistenceException {
    public IncorrectResultSizeException() {
    }

    public IncorrectResultSizeException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public IncorrectResultSizeException(String arg0) {
        super(arg0);
    }

    public IncorrectResultSizeException(Throwable arg0) {
        super(arg0);
    }
}
