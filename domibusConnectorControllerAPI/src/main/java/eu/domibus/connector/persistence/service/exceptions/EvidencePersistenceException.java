/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.exceptions;

/**
 * This exception is thrown when there is an error in the persistence of evidence. It extends the
 * PersistenceException class.
 */
public class EvidencePersistenceException extends PersistenceException {
    public EvidencePersistenceException() {
    }

    public EvidencePersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public EvidencePersistenceException(String arg0) {
        super(arg0);
    }

    public EvidencePersistenceException(Throwable arg0) {
        super(arg0);
    }
}
