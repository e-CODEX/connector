/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.exceptions;

/**
 * This exception is thrown when there is an attempt to persist duplicate evidence.
 * It extends the EvidencePersistenceException class.
 */
public class DuplicateEvidencePersistenceException extends EvidencePersistenceException {
    public DuplicateEvidencePersistenceException() {
    }

    public DuplicateEvidencePersistenceException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public DuplicateEvidencePersistenceException(String arg0) {
        super(arg0);
    }

    public DuplicateEvidencePersistenceException(Throwable arg0) {
        super(arg0);
    }
}
