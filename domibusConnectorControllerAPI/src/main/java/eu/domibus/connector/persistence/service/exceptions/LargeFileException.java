/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.exceptions;

/**
 * Represents an exception that occurs when handling large files.
 */
public class LargeFileException extends PersistenceException {
    public LargeFileException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
